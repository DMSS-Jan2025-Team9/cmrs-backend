package com.example.usermanagement.validation;

import java.util.ArrayList;
import java.util.List;

import com.example.usermanagement.model.Student;

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
