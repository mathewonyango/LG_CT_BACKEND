package com.livinggoodsbackend.livinggoodsbackend.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.*;
import com.livinggoodsbackend.livinggoodsbackend.Repository.*;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.*;
import com.livinggoodsbackend.livinggoodsbackend.enums.DeliveryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ConversationRepository conversationRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private MessageReadStatusRepository messageReadStatusRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserOnlineStatusRepository userOnlineStatusRepository;
    @Mock private ConversationParticipantRepository conversationParticipantRepository;

    @InjectMocks
    private ChatService chatService;

    private User testUser;
    private Conversation conversation;
    private Message message;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john");
        testUser.setEmail("john@example.com");

        conversation = new Conversation();
        conversation.setId("conv1");
        conversation.setIsGroup(false);
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setCreatedAt(LocalDateTime.now());

        message = new Message();
        message.setId("msg1");
        message.setContent("Hi there");
        message.setSender(testUser);
        message.setConversation(conversation);
        message.setReadStatuses(new ArrayList<>());
        message.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getUserConversations_ShouldReturnConversationDtos() {
        when(conversationRepository.findByUserIdOrderByUpdatedAtDesc(1L))
                .thenReturn(Collections.singletonList(conversation));
        when(messageRepository.countUnreadMessagesForUser(anyString(), anyLong())).thenReturn(0L);

        List<ConversationDto> result = chatService.getUserConversations(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("conv1", result.get(0).getId());
        verify(conversationRepository).findByUserIdOrderByUpdatedAtDesc(1L);
    }

    @Test
    void createConversation_ShouldCreateNewConversation() {
        CreateConversationRequest req = new CreateConversationRequest();
        req.setIsGroup(false);
        req.setParticipantIds(Arrays.asList(1L, 2L));

        User user2 = new User();
        user2.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(conversationRepository.save(any())).thenReturn(conversation);

        ConversationDto dto = chatService.createConversation(req);

        assertNotNull(dto);
        assertEquals(conversation.getId(), dto.getId());
        verify(conversationRepository, atLeastOnce()).save(any());
    }

    @Test
    void getConversationMessages_ShouldReturnPagedMessages() {
        Page<Message> page = new PageImpl<>(Collections.singletonList(message));
        when(messageRepository.findConversationMessages(anyString(), any(Pageable.class)))
                .thenReturn(page);

        MessageReadStatus rs = new MessageReadStatus();
        rs.setUser(testUser);
        rs.setStatus(DeliveryStatus.SENT);
        message.setReadStatuses(Collections.singletonList(rs));

        MessagePageDto result = chatService.getConversationMessages("conv1", 1L, 0, 10);

        assertNotNull(result);
        assertFalse(result.getMessages().isEmpty());
        verify(messageRepository).findConversationMessages(eq("conv1"), any(Pageable.class));
        verify(messageReadStatusRepository).save(any(MessageReadStatus.class));
    }

    @Test
    void sendMessage_ShouldSaveMessageAndReturnDto() {
        SendMessageRequest req = new SendMessageRequest();
        req.setConversationId("conv1");
        req.setSenderId(1L);
        req.setContent("Hello");

        ConversationParticipant p1 = new ConversationParticipant();
        p1.setUser(testUser);
        conversation.setParticipants(Collections.singletonList(p1));

        when(conversationRepository.findById("conv1")).thenReturn(Optional.of(conversation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(messageRepository.save(any())).thenReturn(message);
        when(conversationRepository.save(any())).thenReturn(conversation);

        MessageDto result = chatService.sendMessage(req);

        assertNotNull(result);
        assertEquals("conv1", result.getConversationId());
        verify(messageRepository).save(any());
    }

    @Test
    void markMessagesAsRead_ShouldMarkAllMessages() {
        MarkReadRequest req = new MarkReadRequest();
        req.setUserId(1L);
        req.setMessageIds(List.of("msg1"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(messageRepository.findById("msg1")).thenReturn(Optional.of(message));
        when(messageReadStatusRepository.findByMessageIdAndUserId("msg1", 1L)).thenReturn(Optional.empty());

        MarkReadResponse res = chatService.markMessagesAsRead(req);

        assertEquals(1, res.getMarkedCount());
        verify(messageReadStatusRepository, times(1)).save(any());
    }

    @Test
    void updateUserStatus_ShouldMarkPendingDeliveredWhenOnline() {
        UpdateStatusRequest req = new UpdateStatusRequest();
        req.setIsOnline(true);

        UserOnlineStatus status = new UserOnlineStatus();
        status.setIsOnline(false);
        testUser.setOnlineStatus(status);

        MessageReadStatus pending = new MessageReadStatus();
        pending.setStatus(DeliveryStatus.SENT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(messageReadStatusRepository.findByUserIdAndStatus(1L, DeliveryStatus.SENT))
                .thenReturn(List.of(pending));

        UserStatusDto result = chatService.updateUserStatus(1L, req);

        assertNotNull(result);
        assertTrue(result.getIsOnline());
        verify(messageReadStatusRepository).saveAll(any());
    }

    @Test
    void updateAllUserStatuses_ShouldUpdateStatuses() {
        User u = new User();
        u.setId(2L);
        u.setOnlineStatus(new UserOnlineStatus());
        when(userRepository.findAll()).thenReturn(List.of(testUser, u));

        int result = chatService.updateAllUserStatuses(true);

        assertTrue(result > 0);
        verify(userOnlineStatusRepository, atLeastOnce()).save(any());
    }

    @Test
    void updateHeartbeat_ShouldUpdateExistingStatus() {
        UserOnlineStatus status = new UserOnlineStatus();
        status.setUser(testUser);

        when(userOnlineStatusRepository.findById(1L)).thenReturn(Optional.of(status));
        when(userOnlineStatusRepository.save(any())).thenReturn(status);

        UserOnlineStatus result = chatService.updateHeartbeat(1L);

        assertNotNull(result);
        assertTrue(result.getIsOnline());
        verify(userOnlineStatusRepository).save(any());
    }

    @Test
    void getUserLastSeen_ShouldReturnOptionalStatus() {
        UserOnlineStatus status = new UserOnlineStatus();
        when(userOnlineStatusRepository.findById(1L)).thenReturn(Optional.of(status));

        Optional<UserOnlineStatus> result = chatService.getUserLastSeen(1L);

        assertTrue(result.isPresent());
        verify(userOnlineStatusRepository).findById(1L);
    }

    @Test
    void searchUsers_ShouldReturnUserDtos() {
        when(userRepository.searchUsers("john", 1L)).thenReturn(List.of(testUser));

        List<UserDto> result = chatService.searchUsers("john", 1L);

        assertEquals(1, result.size());
        assertEquals("john@example.com", result.get(0).getEmail());
    }
}
