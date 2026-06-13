package com.example.useractivity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ActivityEvent {

    private String userId;

    @NotBlank
    @Pattern(regexp = "PAGE_VIEW|CLICK|PURCHASE|LOGOUT")
    private String action;

    @NotBlank
    private String resourceId;

    @NotNull
    private Long timestamp;

    public ActivityEvent() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}