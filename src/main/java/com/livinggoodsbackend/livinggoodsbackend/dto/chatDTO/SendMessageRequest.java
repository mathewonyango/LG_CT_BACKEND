package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;
import com.livinggoodsbackend.livinggoodsbackend.enums.MessageType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {
    private String conversationId;
    private Long senderId;
    private String content;
    private MessageType messageType;
  
    
}
