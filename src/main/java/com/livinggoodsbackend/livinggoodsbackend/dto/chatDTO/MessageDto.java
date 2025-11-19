package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;

import com.livinggoodsbackend.livinggoodsbackend.enums.MessageType;
import com.livinggoodsbackend.livinggoodsbackend.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String id;
    private String conversationId;
    private Long senderId;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;
    private Boolean isRead;             // For current user
    private DeliveryStatus deliveryStatus;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private List<ReadByDto> readBy;     // All users who read it
}
