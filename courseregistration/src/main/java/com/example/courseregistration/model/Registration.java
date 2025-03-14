package com.example.courseregistration.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationId;

    private Long studentId;

    private Long classId;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime registeredAt;

    private String registrationStatus;

    private Long groupRegistrationId;

    // Getters and Setters
    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
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

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public Long getGroupRegistrationId() {
        return groupRegistrationId;
    }

    public void setGroupRegistrationId(Long groupRegistrationId) {
        this.groupRegistrationId = groupRegistrationId;
    }
}
