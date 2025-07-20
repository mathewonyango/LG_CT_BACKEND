package com.livinggoodsbackend.livinggoodsbackend.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.livinggoodsbackend.livinggoodsbackend.Model.User;

@Service
public class UserKafkaProducer {

     private static final String TOPIC = "user-topic";
    
    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;
    
    public void sendAllUsersToKafka(List<User> users) {
        System.out.println("🚀 SENDING " + users.size() + " USERS TO KAFKA...");
        
        for (User user : users) {
            kafkaTemplate.send(TOPIC, user);
            System.out.println("🚀 Sent: " + user);
        }
        
        System.out.println("🎉 ALL " + users.size() + " USERS SENT TO KAFKA!");
    }

    public void sendUserToKafka(User user) {
    kafkaTemplate.send("user-topic", user);
}

}
