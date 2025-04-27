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
public class StudentUpdateRequestDto {
    private String email;
    private String firstName;
    private String lastName;
    private String studentFullId;
    private String programName;
    private List<String> roles;
}