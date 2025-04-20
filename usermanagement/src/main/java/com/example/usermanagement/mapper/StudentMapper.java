package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.dto.StudentResponseDto;
import com.example.usermanagement.dto.StudentUpdateRequestDto;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class StudentMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static StudentDto toDto(Student student) {
        StudentDto dto = new StudentDto();
        dto.setName(student.getName());
        dto.setStudentId(String.valueOf(student.getStudentId()));
        dto.setStudentFullId(student.getStudentFullId());
        dto.setProgramName(student.getProgramName());
        if (student.getEnrolledAt() != null) {
            dto.setEnrolledAt(
                    student.getEnrolledAt()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .format(DATE_FORMATTER));
        }
        return dto;
    }

    public static StudentResponseDto toResponseDto(Student student) {
        if (student == null) {
            return null;
        }

        User user = student.getUser();
        List<String> roles = null;

        if (user != null && user.getRoles() != null) {
            roles = user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
        }

        String enrolledAtFormatted = null;
        if (student.getEnrolledAt() != null) {
            enrolledAtFormatted = student.getEnrolledAt()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DATE_FORMATTER);
        }

        return StudentResponseDto.builder()
                .studentId(student.getStudentId())
                .userId(user != null ? user.getUserId() : null)
                .username(user != null ? user.getUsername() : null)
                .email(user != null ? user.getEmail() : null)
                .fullName(student.getName())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .studentIdNumber(String.valueOf(student.getStudentId()))
                .studentFullId(student.getStudentFullId())
                .programName(student.getProgramName())
                .enrolledAt(enrolledAtFormatted)
                .roles(roles)
                .build();
    }

    public static void updateStudentFromDto(Student student, StudentUpdateRequestDto dto) {
        if (student == null || dto == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            student.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            student.setLastName(dto.getLastName());
        }

        if (dto.getFirstName() != null && dto.getLastName() != null) {
            student.setName(dto.getFirstName() + " " + dto.getLastName());
        }

        if (dto.getStudentFullId() != null) {
            student.setStudentFullId(dto.getStudentFullId());
        }

        if (dto.getProgramName() != null) {
            student.setProgramName(dto.getProgramName());
        }

        // Update user email if provided
        if (dto.getEmail() != null && student.getUser() != null) {
            student.getUser().setEmail(dto.getEmail());
        }

        // Note: Roles will be handled separately in the service layer
    }
}
