package com.example.usermanagement.dto;

import lombok.Data;

@Data
public class StudentUserRegistrationDto {
    private UserRegistrationDto user;
    private StudentRegistrationDto student;

}

