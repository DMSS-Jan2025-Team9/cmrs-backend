package com.example.usermanagement.validation;

import com.example.usermanagement.model.Student;

public class LastNameValidator implements StudentValidationHandler {
    @Override
    public void validate(Student student) {
        if (student.getLastName() == null || student.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last Name cannot be empty");
        }
    }
}
