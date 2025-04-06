package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.model.Student;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
                            .format(DATE_FORMATTER)
            );
        }
        return dto;
    }
}
