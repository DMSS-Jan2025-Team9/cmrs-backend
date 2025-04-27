package com.example.usermanagement.factory;

import com.example.usermanagement.dto.UserRegistrationDto;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UserRegistrationFactory {
    
    public UserRegistrationDto createRegistrationDto(List<String> role) {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setRole(role);
        return userRegistrationDto;
    }

    public String generateStudentId(Integer studentId) {
        String paddedId = (studentId < 10) ? String.format("%02d", studentId) : studentId.toString();
        int randomPadding = 1000 + (int) (Math.random() * 9000);
        return "U" + paddedId + randomPadding;
    }

    public String generateStaffId(Integer staffId) {
        String paddedId = (staffId < 10) ? String.format("%02d", staffId) : staffId.toString();
        int randomPadding = 1000 + (int) (Math.random() * 9000);
        return "S" + paddedId + randomPadding;
    }
} 