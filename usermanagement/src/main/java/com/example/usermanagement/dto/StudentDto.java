package com.example.usermanagement.dto;

import lombok.Data;

@Data
public class StudentDto {
    private String name;
    private Long studentId;
    private String studentFullId;
    private String programName;
    private String enrolledAt;
}
