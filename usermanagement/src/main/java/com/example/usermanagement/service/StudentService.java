package com.example.usermanagement.service;

import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.mapper.StudentMapper;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<StudentDto> findStudentsByProgram(String programName) {
        return studentRepository.findByProgramName(programName).stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }
}
