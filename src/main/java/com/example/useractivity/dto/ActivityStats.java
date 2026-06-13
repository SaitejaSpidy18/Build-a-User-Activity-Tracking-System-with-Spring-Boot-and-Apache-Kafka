package com.example.useractivity.dto;

import java.util.Map;

public class ActivityStats {
    private long totalEvents;
    private Map<String, Long> eventsByAction;

    public ActivityStats() {
    }

    public ActivityStats(long totalEvents, Map<String, Long> eventsByAction) {
        this.totalEvents = totalEvents;
        this.eventsByAction = eventsByAction;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Map<String, Long> getEventsByAction() {
        return eventsByAction;
    }

    public void setEventsByAction(Map<String, Long> eventsByAction) {
        this.eventsByAction = eventsByAction;
    }
}
