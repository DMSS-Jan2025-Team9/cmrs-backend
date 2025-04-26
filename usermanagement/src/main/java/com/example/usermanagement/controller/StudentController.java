package com.example.usermanagement.controller;

import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.mapper.StudentMapper;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    // Get all students
//    @GetMapping
//    public List<Student> getStudents() {
//        return studentRepository.findAll();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
//        return studentRepository.findById(id)
//            .map(student -> ResponseEntity.ok(student))
//            .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/program/{programName}")
//    public List<Student> getStudentsByProgram(@PathVariable String programName) {
//        return studentService.findStudentsByProgram(programName);
//    }

    // GET all students as DTOs
    @GetMapping
    public List<StudentDto> getStudents() {
        return studentRepository.findAll().stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    // GET a single student by ID as DTO
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudent(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(StudentMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Get a single student bystudentFullId
    @GetMapping("/studentFullId/{studentFullId}")
    public ResponseEntity<StudentDto> getStudentByStudentFullId(@PathVariable String studentFullId) {
        return studentRepository.findBystudentFullId(studentFullId)
                .map(StudentMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET all students by program name as DTOs
    @GetMapping("/program/{programName}")
    public List<StudentDto> getStudentsByProgram(@PathVariable String programName) {
        return studentService.findStudentsByProgram(programName);
    }

}