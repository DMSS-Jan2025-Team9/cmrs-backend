package com.example.usermanagement.service;

import java.util.List;

import com.example.usermanagement.dto.PasswordUpdateDto;
import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.dto.StudentResponseDto;
import com.example.usermanagement.dto.StudentUpdateRequestDto;
import com.example.usermanagement.model.Student;

public interface StudentService {
    List<Student> getAllStudents();

    Student getStudentByUserId(Integer userId);

    StudentResponseDto getStudentResponseByUserId(Integer userId);

    List<StudentDto> findStudentsByProgram(String programName);

    List<StudentResponseDto> getAllStudentResponses();

    StudentResponseDto updateStudent(Integer userId, StudentUpdateRequestDto updateDto);

    boolean updatePassword(Integer userId, PasswordUpdateDto passwordUpdateDto);

    void deleteStudent(Integer userId);
}
