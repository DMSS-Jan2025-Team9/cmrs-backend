package com.example.usermanagement.strategy;

import org.springframework.stereotype.Component;

@Component
public class StaffEmailStrategy implements EmailGenerationStrategy {
    @Override
    public String generateEmail(String identifier) {
        return identifier + "@staff.university.edu";
    }
} 