// package com.livinggoodsbackend.livinggoodsbackend.config;

// import java.util.HashMap;
// import java.util.Map;

// import org.apache.kafka.clients.producer.ProducerConfig;
// import org.apache.kafka.common.serialization.StringSerializer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.kafka.core.DefaultKafkaProducerFactory;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.core.ProducerFactory;
// import org.springframework.kafka.support.serializer.JsonSerializer;

// public class KafkaConfig {
//      @Bean
//     public ProducerFactory<String, Object> producerFactory() {
//         Map<String, Object> configProps = new HashMap<>();
//         configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
//         configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//         configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//         configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false); // Optional: reduces message size
//         return new DefaultKafkaProducerFactory<>(configProps);
//     }

//     @Bean
//     public KafkaTemplate<String, Object> kafkaTemplate() {
//         return new KafkaTemplate<>(producerFactory());
//     }

// }
