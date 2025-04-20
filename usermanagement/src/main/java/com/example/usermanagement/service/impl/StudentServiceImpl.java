package com.example.usermanagement.service.impl;

import com.example.usermanagement.dto.PasswordUpdateDto;
import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.dto.StudentResponseDto;
import com.example.usermanagement.dto.StudentUpdateRequestDto;
import com.example.usermanagement.mapper.StudentMapper;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<StudentDto> findStudentsByProgram(String programName) {
        return studentRepository.findByProgramName(programName).stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentByUserId(Integer userId) {
        return studentRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + userId));
    }

    @Override
    public StudentResponseDto getStudentResponseByUserId(Integer userId) {
        Student student = getStudentByUserId(userId);
        return StudentMapper.toResponseDto(student);
    }

    @Override
    public List<StudentResponseDto> getAllStudentResponses() {
        return studentRepository.findAll().stream()
                .map(StudentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudent(Integer userId, StudentUpdateRequestDto updateDto) {
        Student student = getStudentByUserId(userId);

        // Update the student entity from the DTO
        StudentMapper.updateStudentFromDto(student, updateDto);

        // Update roles if provided
        if (updateDto.getRoles() != null && !updateDto.getRoles().isEmpty() && student.getUser() != null) {
            User user = student.getUser();
            Set<Role> newRoles = updateDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByRoleName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(newRoles);
            userRepository.save(user);
        }

        // Save the updated student
        student = studentRepository.save(student);

        // If email was updated in the user entity
        if (updateDto.getEmail() != null && student.getUser() != null) {
            userRepository.save(student.getUser());
        }

        return StudentMapper.toResponseDto(student);
    }

    @Override
    @Transactional
    public boolean updatePassword(Integer userId, PasswordUpdateDto passwordUpdateDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Verify current password matches
        if (!passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(), user.getPassword())) {
            return false;
        }

        // Verify new password and confirm password match
        if (!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmPassword())) {
            return false;
        }

        // Encode and update the password
        user.setPassword(passwordEncoder.encode(passwordUpdateDto.getNewPassword()));
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public void deleteStudent(Integer userId) {
        studentRepository.deleteByUser_UserId(userId);
    }
}