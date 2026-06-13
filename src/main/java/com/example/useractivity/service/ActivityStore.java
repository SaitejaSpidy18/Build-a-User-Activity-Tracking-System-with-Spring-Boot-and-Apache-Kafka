package com.example.useractivity.service;

import com.example.useractivity.dto.ActivityEvent;
import com.example.useractivity.dto.ActivityStats;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActivityStore {

    private final Map<String, List<ActivityEvent>> userEvents = new ConcurrentHashMap<>();
    private final Map<String, Long> eventsByAction = new ConcurrentHashMap<>();

    private long totalEvents = 0;

    public synchronized void storeEvent(ActivityEvent event) {
        String userId = event.getUserId();
        List<ActivityEvent> events = userEvents.computeIfAbsent(userId, k -> new ArrayList<>());
        events.add(event);

        totalEvents++;

        String action = event.getAction();
        eventsByAction.merge(action, 1L, Long::sum);
    }

    public List<ActivityEvent> getEventsByUserId(String userId) {
        return userEvents.getOrDefault(userId, new ArrayList<>());
    }

    public synchronized ActivityStats getStats() {
        return new ActivityStats(totalEvents, new ConcurrentHashMap<>(eventsByAction));
    }
}