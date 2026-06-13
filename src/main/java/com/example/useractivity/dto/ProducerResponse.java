package com.example.useractivity.dto;

public class ProducerResponse {
    private String status;
    private String eventId;

    public ProducerResponse() {
    }

    public ProducerResponse(String status, String eventId) {
        this.status = status;
        this.eventId = eventId;
    }

    public String getStatus() {
        return status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
