package com.livinggoodsbackend.livinggoodsbackend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.dto.UserRegisteredDTO;

@Service
public class WebSocketSender {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendUserEvent(UserRegisteredDTO dto) {
        messagingTemplate.convertAndSend("/topic/user-events", dto);
    }
}