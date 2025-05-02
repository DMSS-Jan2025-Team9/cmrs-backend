package com.example.courseregistration.dto;

import java.time.LocalDateTime;

public class RegistrationDTO {
    private Long registrationId;
    private Long studentId;
    private Long classId;
    private LocalDateTime registeredAt;
    private String registrationStatus;
    private Long groupRegistrationId;

    public RegistrationDTO() {}

    public RegistrationDTO(Long registrationId, Long studentId, Long classId, LocalDateTime registeredAt,String registrationStatus,Long groupRegistrationId) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.classId = classId;
        this.registeredAt = registeredAt;
        this.registrationStatus = registrationStatus;
        this.groupRegistrationId = groupRegistrationId;
    }

    public Long getRegistrationId() {
        return registrationId;
    }
    public Long getStudentId() {
        return studentId;
    }

    public Long getClassId() {
        return classId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }


    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public Long getGroupRegistrationId() {
        return groupRegistrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }
}