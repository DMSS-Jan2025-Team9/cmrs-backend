package com.example.notificationservice.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String studentFullId;

    private Long userId; // Keep for backward compatibility

    private String notificationMessage;

    private Timestamp createdAt;

    private Timestamp sentAt;

    private Timestamp readAt;

    // Getters and Setters
    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getStudentFullId() {
        return studentFullId;
    }

    public void setStudentFullId(String studentFullId) {
        this.studentFullId = studentFullId;
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

    public Timestamp getReadAt() {
        return readAt;
    }

    public void setReadAt(Timestamp readAt) {
        this.readAt = readAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", studentFullId='" + studentFullId + '\'' +
                ", userId=" + userId +
                ", notificationMessage='"
                + (notificationMessage != null
                        ? notificationMessage.substring(0, Math.min(notificationMessage.length(), 50)) + "..."
                        : "null")
                + '\'' +
                ", createdAt=" + createdAt +
                ", sentAt=" + sentAt +
                ", readAt=" + readAt +
                '}';
    }
}
