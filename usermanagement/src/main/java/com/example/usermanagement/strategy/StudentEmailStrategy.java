package com.example.usermanagement.strategy;

import org.springframework.stereotype.Component;

@Component
public class StudentEmailStrategy implements EmailGenerationStrategy {
    @Override
    public String generateEmail(String identifier) {
        return identifier + "@university.edu";
    }
} 