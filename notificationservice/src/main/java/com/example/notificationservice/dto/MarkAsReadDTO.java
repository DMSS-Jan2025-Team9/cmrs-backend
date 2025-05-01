package com.example.notificationservice.dto;

import java.util.List;

public class MarkAsReadDTO {
    private List<Long> notificationIds;

    // Default constructor for Jackson
    public MarkAsReadDTO() {
    }

    public MarkAsReadDTO(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }

    public List<Long> getNotificationIds() {
        return notificationIds;
    }

    public void setNotificationIds(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }
}
