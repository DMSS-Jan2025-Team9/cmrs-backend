package com.example.courseregistration.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private String name;
    private Long studentId;
    private String studentFullId;
    private String programName;
    private String enrolledAt;

    public StudentDTO() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentFullId() {
        return studentFullId;
    }

}