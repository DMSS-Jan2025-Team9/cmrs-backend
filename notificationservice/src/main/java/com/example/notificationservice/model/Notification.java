package com.example.notificationservice.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String userFullId;

    private Long userId; // Keep for backward compatibility

    private String notificationMessage;

    private Timestamp createdAt;

    private Timestamp sentAt;

    // Getters and Setters
    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserFullId() {
        return userFullId;
    }

    public void setUserFullId(String userFullId) {
        this.userFullId = userFullId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userFullId='" + userFullId + '\'' +
                ", userId=" + userId +
                ", notificationMessage='"
                + (notificationMessage != null
                        ? notificationMessage.substring(0, Math.min(notificationMessage.length(), 50)) + "..."
                        : "null")
                + '\'' +
                ", createdAt=" + createdAt +
                ", sentAt=" + sentAt +
                '}';
    }
}
