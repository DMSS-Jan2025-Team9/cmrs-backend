package com.example.coursemanagement.exception;

public class DuplicateCourseCodeException extends RuntimeException {
    public DuplicateCourseCodeException(String message) {
        super(message);
    }
}