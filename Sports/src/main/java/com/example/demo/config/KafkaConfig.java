package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic sportyEventsTopic() {
        return TopicBuilder.name("sporty-event-updates")
                .partitions(3)    // Good for scalability
                .replicas(1)      
                .build();
    }
}
