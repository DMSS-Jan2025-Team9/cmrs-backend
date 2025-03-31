package com.example.usermanagement.controller;

import com.example.usermanagement.dto.Student;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    // Get all students
    @GetMapping
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return studentRepository.findById(id)
            .map(student -> ResponseEntity.ok(student))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/program/{programName}")
    public List<Student> getStudentsByProgram(@PathVariable String programName) {
        return studentService.findStudentsByProgram(programName);
    }


}