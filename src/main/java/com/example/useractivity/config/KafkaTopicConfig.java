package com.example.useractivity.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userActivityEventsTopic() {
        return TopicBuilder.name("user-activity-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userActivityEventsDltTopic() {
        return TopicBuilder.name("user-activity-events.DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
