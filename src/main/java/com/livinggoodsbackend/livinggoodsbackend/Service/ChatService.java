package com.livinggoodsbackend.livinggoodsbackend.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.*;
import com.livinggoodsbackend.livinggoodsbackend.Repository.*;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.*;
import com.livinggoodsbackend.livinggoodsbackend.enums.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;



@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final MessageReadStatusRepository messageReadStatusRepository;
    private final UserRepository userRepository;
    private final UserOnlineStatusRepository userOnlineStatusRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;


    private static final int OFFLINE_THRESHOLD_MINUTES = 2;


    // ====== Conversations ======
    public List<ConversationDto> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);

        return conversations.stream()
                .map(conv -> mapToConversationDto(conv, userId))
                .collect(Collectors.toList());
    }

    public ConversationDto createConversation(CreateConversationRequest request) {
        if (!request.getIsGroup() && request.getParticipantIds().size() == 2) {
            Optional<Conversation> existing = conversationRepository
                    .findPrivateConversationBetweenUsers(
                            request.getParticipantIds().get(0),
                            request.getParticipantIds().get(1)
                    );
            if (existing.isPresent()) {
                return mapToConversationDto(existing.get(), request.getParticipantIds().get(0));
            }
        }

        Conversation conversation = new Conversation();
        conversation.setId(UUID.randomUUID().toString());
        conversation.setIsGroup(request.getIsGroup());
        conversation.setGroupName(request.getGroupName());
        conversation = conversationRepository.save(conversation);

        for (Long participantId : request.getParticipantIds()) {
            User user = userRepository.findById(participantId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + participantId));

            ConversationParticipant participant = new ConversationParticipant();
            participant.setConversation(conversation);
            participant.setUser(user);
            participant.setIsActive(true);
            conversationParticipantRepository.save(participant);
        }

        return mapToConversationDto(conversation, request.getParticipantIds().get(0));
    }

    // ====== Messages ======
  public MessagePageDto getConversationMessages(String conversationId, Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Message> messagePage = messageRepository.findConversationMessages(conversationId, pageable);

    List<MessageDto> messages = messagePage.getContent().stream()
            .map(m -> {
                // update status for this user when pulling
                m.getReadStatuses().stream()
                        .filter(rs -> rs.getUser().getId().equals(userId))  // only recipient
                        .filter(rs -> rs.getStatus() == DeliveryStatus.SENT) // not already delivered/read
                        .forEach(rs -> {
                            rs.markDelivered(); // sets status + deliveredAt
                            messageReadStatusRepository.save(rs);
                        });

                return mapToMessageDto(m, userId);
            })
            .collect(Collectors.toList());

    PaginationDto pagination = new PaginationDto(
            page, size, messagePage.getTotalElements(), messagePage.getTotalPages(),
            messagePage.hasNext(), messagePage.hasPrevious()
    );

    return new MessagePageDto(messages, pagination);
}

   public MessageDto sendMessage(SendMessageRequest request) {
    Message message = new Message();
    message.setId(UUID.randomUUID().toString());

    Conversation conversation = conversationRepository.findById(request.getConversationId())
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
    User sender = userRepository.findById(request.getSenderId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    message.setConversation(conversation);
    message.setSender(sender);
    message.setContent(request.getContent());
    message.setMessageType(request.getMessageType());
    message.setIsDeleted(false);

    // Init delivery statuses (SENT or DELIVERED depending on recipient online status)
    List<MessageReadStatus> statuses = new ArrayList<>();
    for (ConversationParticipant participant : conversation.getParticipants()) {
        if (!participant.getUser().getId().equals(sender.getId())) {
            MessageReadStatus status = new MessageReadStatus();
            status.setMessage(message);
            status.setUser(participant.getUser());

            // Check if recipient is online
            boolean isOnline = participant.getUser().getOnlineStatus() != null &&
                               Boolean.TRUE.equals(participant.getUser().getOnlineStatus().getIsOnline());

            if (isOnline) {
                status.markDelivered(); // sets DELIVERED + deliveredAt
            } else {
                status.setStatus(DeliveryStatus.SENT);
            }

            statuses.add(status);
        }
    }
    message.setReadStatuses(statuses);

    message = messageRepository.save(message);

    conversation.setUpdatedAt(LocalDateTime.now());
    conversationRepository.save(conversation);

    return mapToMessageDto(message, sender.getId());
}


    @Transactional
    public MarkReadResponse markMessagesAsRead(MarkReadRequest request) {
        int markedCount = 0;

        log.info("Marking messages as read: {}", request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (String messageId : request.getMessageIds()) {
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));

            Optional<MessageReadStatus> existingStatusOpt =
                    messageReadStatusRepository.findByMessageIdAndUserId(messageId, request.getUserId());

            if (existingStatusOpt.isPresent()) {
                MessageReadStatus existingStatus = existingStatusOpt.get();
                if (existingStatus.getStatus() != DeliveryStatus.READ) {
                    existingStatus.setStatus(DeliveryStatus.READ);
                    existingStatus.setReadAt(LocalDateTime.now());
                    messageReadStatusRepository.save(existingStatus);
                    markedCount++;
                }
            } else {
                MessageReadStatus readStatus = new MessageReadStatus();
                readStatus.setMessage(message);
                readStatus.setUser(user);
                readStatus.setStatus(DeliveryStatus.READ);
                readStatus.setReadAt(LocalDateTime.now());
                messageReadStatusRepository.save(readStatus);
                markedCount++;
            }
        }

        return new MarkReadResponse(markedCount, request.getMessageIds());
    }

    // ====== User Status ======
    public List<UserStatusDto> getOnlineUsers() {
        List<User> onlineUsers = userRepository.findOnlineUsers();
        return onlineUsers.stream()
                .map(this::mapToUserStatusDto)
                .collect(Collectors.toList());
    }

    @Transactional
public UserStatusDto updateUserStatus(Long userId, UpdateStatusRequest request) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    UserOnlineStatus status = user.getOnlineStatus();
    if (status == null) {
        status = new UserOnlineStatus();
        status.setUser(user);
        user.setOnlineStatus(status);
    }

    status.setIsOnline(request.getIsOnline());
    status.setLastSeen(Instant.now());

    userOnlineStatusRepository.save(status);

    // If user just went ONLINE, mark their pending messages as DELIVERED
    if (Boolean.TRUE.equals(request.getIsOnline())) {
        List<MessageReadStatus> pending = messageReadStatusRepository
                .findByUserIdAndStatus(userId, DeliveryStatus.SENT);

        for (MessageReadStatus rs : pending) {
            rs.markDelivered(); // sets status=DELIVERED + deliveredAt=now
        }

        if (!pending.isEmpty()) {
            messageReadStatusRepository.saveAll(pending);
            log.info("Marked {} messages as DELIVERED for user {}", pending.size(), userId);
        }
    }

    return mapToUserStatusDto(user);
}


    @Transactional
    public int updateAllUserStatuses(boolean isOnline) {
        List<User> allUsers = userRepository.findAll();
        int updatedCount = 0;

        for (User user : allUsers) {
            UserOnlineStatus status = user.getOnlineStatus();
            if (status == null) {
                status = new UserOnlineStatus();
                status.setUser(user);
                user.setOnlineStatus(status);
            }
            if (status.getIsOnline() != isOnline) {
                status.setIsOnline(isOnline);
                status.setLastSeen(Instant.now());
                
                userOnlineStatusRepository.save(status);
                updatedCount++;
            }
        }

        return updatedCount;
    }


    // ====== Mapping Helpers ======
    private ConversationDto mapToConversationDto(Conversation conversation, Long currentUserId) {
        List<ConversationParticipant> participantList =
                conversation.getParticipants() != null ? conversation.getParticipants() : Collections.emptyList();

        List<UserStatusDto> participants = participantList.stream()
                .filter(ConversationParticipant::getIsActive)
                .map(p -> mapToUserStatusDto(p.getUser()))
                .collect(Collectors.toList());

        List<Message> messageList =
                conversation.getMessages() != null ? conversation.getMessages() : Collections.emptyList();

        MessageDto lastMessage = messageList.stream()
                .filter(m -> !m.getIsDeleted())
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(m -> mapToMessageDto(m, currentUserId))
                .orElse(null);

        Long unreadCount = messageRepository.countUnreadMessagesForUser(conversation.getId(), currentUserId);

        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        dto.setIsGroup(conversation.getIsGroup());
        dto.setGroupName(conversation.getGroupName());
        dto.setParticipants(participants);
        dto.setLastMessage(lastMessage);
        dto.setUnreadCount(unreadCount != null ? unreadCount.intValue() : 0);
        dto.setLastActivity(conversation.getUpdatedAt());
        dto.setCreatedAt(conversation.getCreatedAt());
        return dto;
    }

    private MessageDto mapToMessageDto(Message message, Long currentUserId) {
        List<ReadByDto> readBy = message.getReadStatuses().stream()
                .map(rs -> new ReadByDto(rs.getUser().getId(), rs.getReadAt()))
                .collect(Collectors.toList());

        boolean isReadByCurrentUser = message.getReadStatuses().stream()
                .anyMatch(rs -> rs.getUser().getId().equals(currentUserId) && rs.getStatus() == DeliveryStatus.READ);

        // Determine overall message delivery status
        DeliveryStatus status;
        if (message.getReadStatuses().stream().anyMatch(rs -> rs.getStatus() == DeliveryStatus.READ)) {
            status = DeliveryStatus.READ;
        } else if (message.getReadStatuses().stream().anyMatch(rs -> rs.getStatus() == DeliveryStatus.DELIVERED)) {
            status = DeliveryStatus.DELIVERED;
        } else {
            status = DeliveryStatus.SENT;
        }

        return new MessageDto(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getContent(),
                message.getMessageType(),
                message.getCreatedAt(),
                isReadByCurrentUser,
                status,
                message.getFileUrl(),
                message.getFileName(),
                message.getFileSize(),
                readBy
        );
    }

  private UserStatusDto mapToUserStatusDto(User user) {
    UserStatusDto dto = new UserStatusDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());

    if (user.getOnlineStatus() != null) {
        dto.setIsOnline(user.getOnlineStatus().getIsOnline());
        dto.setLastSeen(user.getOnlineStatus().getLastSeen()); // keep Instant
    } else {
        dto.setIsOnline(false);
        dto.setLastSeen(null);
    }

    return dto;
}


private UserDto mapToUserDto(User user) {
    return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole()
    );
}

    public List<UserDto> searchUsers(String query, Long excludeUserId) {
        List<User> users = userRepository.searchUsers(query, excludeUserId);
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }
    @Scheduled(fixedRate = 60000) // runs every 1 minute
    public void markInactiveUsersOfflineTask() {
        Instant threshold = Instant.now().minusSeconds(OFFLINE_THRESHOLD_MINUTES * 60);

        int updated = userOnlineStatusRepository.markInactiveUsersOffline(threshold);

        System.out.println("Marked " + updated + " users offline at " + Instant.now());
    }

    public UserOnlineStatus updateHeartbeat(Long userId) {
        log.debug("Updating heartbeat for user: {}", userId);
        
        try {
            Optional<UserOnlineStatus> existingStatus = userOnlineStatusRepository.findById(userId);
            
            if (existingStatus.isPresent()) {
                UserOnlineStatus status = existingStatus.get();
                status.setIsOnline(true);
                status.setLastSeen(Instant.now());
                
                UserOnlineStatus savedStatus = userOnlineStatusRepository.save(status);
                log.debug("Updated existing heartbeat for user: {}", userId);
                return savedStatus;
            } else {
                // Create new status record if doesn't exist
                Optional<User> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    UserOnlineStatus newStatus = new UserOnlineStatus();
                    newStatus.setUserId(userId);
                    newStatus.setUser(user.get());
                    newStatus.setIsOnline(true);
                    newStatus.setLastSeen(Instant.now());

                    
                    UserOnlineStatus savedStatus = userOnlineStatusRepository.save(newStatus);
                    log.debug("Created new heartbeat record for user: {}", userId);
                    return savedStatus;
                } else {
                    log.error("User not found with ID: {}", userId);
                    throw new RuntimeException("User not found with ID: " + userId);
                }
            }
        } catch (Exception e) {
            log.error("Error updating heartbeat for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to update heartbeat for user: " + userId, e);
        }
    }
     public Optional<UserOnlineStatus> getUserLastSeen(Long userId) {
        log.debug("Getting last seen for user: {}", userId);
        return userOnlineStatusRepository.findById(userId);
    }
}
