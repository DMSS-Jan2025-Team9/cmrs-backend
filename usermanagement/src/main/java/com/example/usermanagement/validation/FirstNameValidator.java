package com.example.usermanagement.validation;

import com.example.usermanagement.dto.Student;

public class FirstNameValidator implements StudentValidationHandler {

    @Override
    public void validate(Student student) {
        if (student.getFirstName() == null || student.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First Name cannot be empty");
        }
    }
}
