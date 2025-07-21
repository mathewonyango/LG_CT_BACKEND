package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.User;

@Service
public class UserKafkaConsumer {
     private List<User> receivedUsers = new ArrayList<>();
    
    @KafkaListener(topics = "user-topic", groupId = "user-group")
    public void receiveUser(User user) {
        receivedUsers.add(user);
        // System.out.println("📨 RECEIVED: " + user + " (Total received: " + receivedUsers.size() + ")");
    }
    
    public List<User> getAllReceivedUsers() {
        return new ArrayList<>(receivedUsers);
    }
    
    // public void clearReceivedUsers() {
    //     receivedUsers.clear();
    //     System.out.println("🗑️ CLEARED ALL RECEIVED USERS");
    // }
    
    public int getReceivedCount() {
        return receivedUsers.size();
    }

}
