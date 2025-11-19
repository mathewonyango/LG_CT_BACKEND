package com.livinggoodsbackend.livinggoodsbackend.Controller;

import com.livinggoodsbackend.livinggoodsbackend.Model.UserOnlineStatus;
import com.livinggoodsbackend.livinggoodsbackend.Service.ChatService;
import com.livinggoodsbackend.livinggoodsbackend.dto.ChatApiResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.ConversationDto;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.CreateConversationRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.MarkReadRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.MarkReadResponse;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.MessageDto;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.MessagePageDto;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.SendMessageRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.UpdateAllStatusesRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.UpdateStatusRequest;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.UserDto;
import com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO.UserStatusDto;
import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;

import org.apache.commons.logging.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<ChatApiResponse<List<ConversationDto>>> getUserConversations(@PathVariable Long userId) {
        try {
            List<ConversationDto> conversations = chatService.getUserConversations(userId);
            return ResponseEntity.ok(ChatApiResponse.success("Conversations retrieved successfully", conversations));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ChatApiResponse.error("Failed to retrieve conversations: " + e.getMessage()));
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatApiResponse<MessageDto>> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            MessageDto message = chatService.sendMessage(request);
            return ResponseEntity.ok(ChatApiResponse.success("Message sent successfully", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ChatApiResponse.error("Failed to send message: " + e.getMessage()));
        }
    }

    @PutMapping("/messages/mark-read")
    public ResponseEntity<ChatApiResponse<MarkReadResponse>> markMessagesAsRead(@RequestBody MarkReadRequest request) {
        try {
            MarkReadResponse response = chatService.markMessagesAsRead(request);
            return ResponseEntity.ok(ChatApiResponse.success("Messages marked as read successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ChatApiResponse.error("Failed to mark messages as read: " + e.getMessage()));
        }
    }

    @GetMapping("/users/online")
    public ResponseEntity<ChatApiResponse<List<UserStatusDto>>> getOnlineUsers() {
        try {
            List<UserStatusDto> onlineUsers = chatService.getOnlineUsers();
            return ResponseEntity.ok(ChatApiResponse.success("Online users retrieved successfully", onlineUsers));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ChatApiResponse.error("Failed to retrieve online users: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ChatApiResponse<UserStatusDto>> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UpdateStatusRequest request) {
        try {
            UserStatusDto userStatus = chatService.updateUserStatus(userId, request);
            return ResponseEntity.ok(ChatApiResponse.success("User status updated successfully", userStatus));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ChatApiResponse.error("Failed to update user status: " + e.getMessage()));
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<ChatApiResponse<List<UserDto>>> searchUsers(
            @RequestParam String query,
            @RequestParam Long excludeUserId) {
        try {
            List<UserDto> users = chatService.searchUsers(query, excludeUserId);
            return ResponseEntity.ok(ChatApiResponse.success("Users found successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ChatApiResponse.error("Failed to search users: " + e.getMessage()));
        }
    }

    // create conversation
    @PostMapping("/conversations")
    public ResponseEntity<ChatApiResponse<ConversationDto>> createConversation(
            @RequestBody CreateConversationRequest request) {
        log.info("Creating conversation: {}", request);

        try {
            ConversationDto conversation = chatService.createConversation(request);
            return ResponseEntity.ok(ChatApiResponse.success("Conversation created successfully", conversation));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ChatApiResponse.error("Failed to create conversation: " + e.getMessage()));
        }
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ChatApiResponse<MessagePageDto>> getConversationMessages(
            @PathVariable String conversationId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        MessagePageDto result = chatService.getConversationMessages(conversationId, userId, page, size);
        return ResponseEntity.ok(ChatApiResponse.success("Messages retrieved successfully", result));
    }

    @PutMapping("/users/all/status")
    public ResponseEntity<Integer> updateAllUserStatuses(@RequestBody UpdateAllStatusesRequest request) {
        int updatedCount = chatService.updateAllUserStatuses(request.getIsOnline());
        return ResponseEntity.ok(updatedCount);
    }

    @PostMapping("/{userId}/heartbeat")
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> heartbeat(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, Object> payload) {

        log.debug("Received heartbeat for user: {}", userId);

        try {
            UserOnlineStatus status = chatService.updateHeartbeat(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Heartbeat updated successfully");
            response.put("data", Map.of(
                    "userId", status.getUserId(),
                    "isOnline", status.getIsOnline(),
                    "lastSeen", status.getLastSeen(),
                    "timestamp", LocalDateTime.now()));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing heartbeat for user {}: {}", userId, e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update heartbeat");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
     @GetMapping("/{userId}/lastseen")
    @PreAuthorize("hasRole('CHA') or hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getLastSeen(@PathVariable Long userId) {
        
        log.debug("Getting last seen for user: {}", userId);
        
        try {
            var statusOpt = chatService.getUserLastSeen(userId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (statusOpt.isPresent()) {
                UserOnlineStatus status = statusOpt.get();
                response.put("success", true);
                response.put("data", Map.of(
                    "userId", status.getUserId(),
                    "lastSeen", status.getLastSeen(),
                    "isOnline", status.getIsOnline(),
                    "username", status.getUser().getUsername()
                ));
            } else {
                response.put("success", true);
                response.put("data", Map.of(
                    "userId", userId,
                    "lastSeen", null,
                    "isOnline", false
                ));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error getting last seen for user {}: {}", userId, e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get last seen");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

}
