package com.example.useractivity.service;

import com.example.useractivity.dto.ActivityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ActivityProducer {

    private static final Logger log = LoggerFactory.getLogger(ActivityProducer.class);

    private final KafkaTemplate<String, ActivityEvent> kafkaTemplate;

    public ActivityProducer(KafkaTemplate<String, ActivityEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String sendActivityEvent(ActivityEvent event) {
        String eventId = UUID.randomUUID().toString();
        String topic = "user-activity-events";
        String key = event.getUserId();

        CompletableFuture<SendResult<String, ActivityEvent>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send event for userId={} with eventId={}",
                        event.getUserId(), eventId, ex);
            } else if (result != null) {
                log.info("Event sent successfully for userId={} with eventId={}, partition={}",
                        event.getUserId(), eventId, result.getRecordMetadata().partition());
            }
        });

        return eventId;
    }
}