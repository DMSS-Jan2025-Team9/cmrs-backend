package com.example.courseregistration.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class NotificationEventDTO implements Serializable {
    private String studentFullId;
    private Long studentId; // Keep for backward compatibility
    private Long classId;
    private String courseCode;
    private String courseName;
    private String message;
    private String eventType;

    public NotificationEventDTO() {
    }

    public NotificationEventDTO(String studentFullId, Long studentId, Long classId, String courseCode,
            String courseName, String message,
            String eventType) {
        this.studentFullId = studentFullId;
        this.studentId = studentId;
        this.classId = classId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.message = message;
        this.eventType = eventType;
    }

}