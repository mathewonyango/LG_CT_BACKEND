package com.livinggoodsbackend.livinggoodsbackend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaProducerService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload);
    }
 
    public void sendMessage(String topic, Object payload) {
        kafkaTemplate.send(topic, payload);
    }

}
