package com.example.usermanagement.validation;

import com.example.usermanagement.dto.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentValidationChain {

    private final List<StudentValidationHandler> handlers = new ArrayList<>();

    public StudentValidationChain addHandler(StudentValidationHandler handler) {
        handlers.add(handler);
        return this;
    }

    public void validate(Student student) {
        for (StudentValidationHandler handler : handlers) {
            handler.validate(student);
        }
    }
}
