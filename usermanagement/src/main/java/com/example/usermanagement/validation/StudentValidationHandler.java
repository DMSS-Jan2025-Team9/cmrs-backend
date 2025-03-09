package com.example.usermanagement.validation;

import com.example.usermanagement.model.Student;

public interface StudentValidationHandler {
    void validate(Student student) throws IllegalArgumentException;
}
