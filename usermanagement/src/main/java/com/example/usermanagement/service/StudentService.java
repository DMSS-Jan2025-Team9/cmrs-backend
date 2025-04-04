package com.example.usermanagement.service;

import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findStudentsByProgram(String programName) {
        return studentRepository.findByProgramName(programName);
    }
}
