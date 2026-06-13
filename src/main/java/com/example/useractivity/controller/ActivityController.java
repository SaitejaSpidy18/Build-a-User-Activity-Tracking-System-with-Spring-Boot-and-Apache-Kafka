package com.example.useractivity.controller;

import com.example.useractivity.dto.ActivityEvent;
import com.example.useractivity.dto.ActivityStats;
import com.example.useractivity.dto.ProducerResponse;
import com.example.useractivity.service.ActivityProducer;
import com.example.useractivity.service.ActivityStore;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private final ActivityProducer producer;
    private final ActivityStore store;

    public ActivityController(ActivityProducer producer, ActivityStore store) {
        this.producer = producer;
        this.store = store;
    }

    @PostMapping
    public ResponseEntity<ProducerResponse> postActivity(@Valid @RequestBody ActivityEvent event) {
        String eventId = producer.sendActivityEvent(event);
        ProducerResponse response = new ProducerResponse("ACCEPTED", eventId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ActivityEvent>> getUserActivity(@PathVariable String userId) {
        return ResponseEntity.ok(store.getEventsByUserId(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<ActivityStats> getStats() {
        return ResponseEntity.ok(store.getStats());
    }
}