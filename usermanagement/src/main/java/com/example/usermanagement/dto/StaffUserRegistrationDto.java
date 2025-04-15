package com.example.usermanagement.dto;

import lombok.Data;

@Data
public class StaffUserRegistrationDto {
    private UserRegistrationDto user;
    private StaffRegistrationDto staff;
}
