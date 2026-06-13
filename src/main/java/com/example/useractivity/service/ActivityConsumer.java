package com.example.useractivity.service;

import com.example.useractivity.dto.ActivityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ActivityConsumer {

    private static final Logger log = LoggerFactory.getLogger(ActivityConsumer.class);

    private final ActivityStore store;

    public ActivityConsumer(ActivityStore store) {
        this.store = store;
    }

    @KafkaListener(
            id = "activityConsumer",
            topics = "user-activity-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ActivityEvent event) {
        log.info("Consumed event: userId={}, action={}, resourceId={}, timestamp={}",
                event.getUserId(), event.getAction(), event.getResourceId(), event.getTimestamp());

        if (event.getUserId() == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        store.storeEvent(event);
    }
}