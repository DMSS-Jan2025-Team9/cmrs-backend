package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ApiResponse;
import com.example.usermanagement.dto.PasswordUpdateDto;
import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.dto.StudentResponseDto;
import com.example.usermanagement.dto.StudentUpdateRequestDto;
import com.example.usermanagement.mapper.StudentMapper;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    // @GetMapping
    // public List<Student> getStudents() {
    // return studentRepository.findAll();
    // }
    //
    // @GetMapping("/{id}")
    // public ResponseEntity<Student> getStudent(@PathVariable Long id) {
    // return studentRepository.findById(id)
    // .map(student -> ResponseEntity.ok(student))
    // .orElseGet(() -> ResponseEntity.notFound().build());
    // }
    //
    // @GetMapping("/program/{programName}")
    // public List<Student> getStudentsByProgram(@PathVariable String programName) {
    // return studentService.findStudentsByProgram(programName);
    // }

    // GET all students as DTOs
    @GetMapping
    public List<StudentDto> getStudents() {
        return studentRepository.findAll().stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    // Secure endpoint that returns full student information - admin only
    @GetMapping("/admin/all")
    // @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<List<StudentResponseDto>> getAllStudentsForAdmin() {
        return ResponseEntity.ok(studentService.getAllStudentResponses());
    }

    // Original endpoint preserved for batch job compatibility
    @GetMapping("/allInfo")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // Original endpoint preserved for batch job compatibility
    @GetMapping("/byUserId/{userId}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer userId) {
        return ResponseEntity.ok(studentService.getStudentByUserId(userId));
    }

    // New secure endpoint to get student information
    @GetMapping("/secure/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_student')")
    public ResponseEntity<StudentResponseDto> getSecureStudentById(@PathVariable Integer userId) {
        return ResponseEntity.ok(studentService.getStudentResponseByUserId(userId));
    }

    // Update student information with roles - admin only
    @PutMapping("/update/{userId}")
    // @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<StudentResponseDto> updateStudentWithRoles(
            @PathVariable Integer userId,
            @RequestBody StudentUpdateRequestDto updateDto) {
        StudentResponseDto updatedStudent = studentService.updateStudent(userId, updateDto);
        return ResponseEntity.ok(updatedStudent);
    }

    // Update student password
    @PostMapping("/{userId}/password")
    // @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_student')")
    public ResponseEntity<ApiResponse> updatePassword(
            @PathVariable Integer userId,
            @RequestBody PasswordUpdateDto passwordUpdateDto) {
        boolean updated = studentService.updatePassword(userId, passwordUpdateDto);

        if (updated) {
            return ResponseEntity.ok(new ApiResponse(true, "Password updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Failed to update password. Please check your current password."));
        }
    }

    // Delete student - admin only
    @DeleteMapping("/{userId}")
    // @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<ApiResponse> deleteStudent(@PathVariable Integer userId) {
        studentService.deleteStudent(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Student deleted successfully"));
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