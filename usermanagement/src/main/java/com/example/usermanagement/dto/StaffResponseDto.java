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
public class StaffResponseDto {
    private Integer staffId;
    private Integer userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String staffFullId;
    private String department;
    private String position;
    private List<String> roles;
}