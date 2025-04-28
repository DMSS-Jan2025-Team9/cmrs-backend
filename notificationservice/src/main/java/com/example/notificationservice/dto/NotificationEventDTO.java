package com.example.notificationservice.dto;

import java.io.Serializable;

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

    public String getStudentFullId() {
        return studentFullId;
    }

    public void setStudentFullId(String studentFullId) {
        this.studentFullId = studentFullId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "NotificationEventDTO{" +
                "studentFullId='" + studentFullId + '\'' +
                ", studentId=" + studentId +
                ", classId=" + classId +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", message='"
                + (message != null ? message.substring(0, Math.min(message.length(), 50)) + "..." : "null") + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}