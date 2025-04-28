package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDto {
    private Long studentId;
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private String firstName;
    private String lastName;
    private String studentIdNumber;
    private String studentFullId;
    private String programName;
    private String enrolledAt;
    private List<String> roles;
}