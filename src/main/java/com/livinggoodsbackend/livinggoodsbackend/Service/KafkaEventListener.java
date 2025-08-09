// package com.livinggoodsbackend.livinggoodsbackend.Service;

// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;

// import com.livinggoodsbackend.livinggoodsbackend.dto.UserDTO;
// import com.livinggoodsbackend.livinggoodsbackend.dto.UserRegisteredDTO;
// import com.livinggoodsbackend.livinggoodsbackend.Service.WebSocketSender;

// @Service
// public class KafkaEventListener {

//     private final WebSocketSender webSocketSender;

//     public KafkaEventListener(WebSocketSender webSocketSender) {
//         this.webSocketSender = webSocketSender;
//     }

//     @KafkaListener(topics = "user-events", containerFactory = "userKafkaListenerContainerFactory")
//     public void handleUserEvent(UserRegisteredDTO dto) {
//         System.out.println("Kafka user-event received: " + dto.getUsername());
//         webSocketSender.sendUserEvent(dto); // Send via WebSocket
//     }

// }
