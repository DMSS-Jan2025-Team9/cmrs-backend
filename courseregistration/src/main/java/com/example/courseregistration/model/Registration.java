package com.example.courseregistration.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationId;

    private Long studentId;

    private Long courseId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt = new Date();

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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
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
