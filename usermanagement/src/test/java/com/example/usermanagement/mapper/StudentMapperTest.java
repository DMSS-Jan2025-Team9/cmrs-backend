package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.dto.StudentResponseDto;
import com.example.usermanagement.dto.StudentUpdateRequestDto;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StudentMapperTest {

    @Test
    public void testToDto() {
        // Arrange
        Student student = new Student();
        student.setStudentId(1L);
        student.setName("John Doe");
        student.setStudentFullId("U123456");
        student.setProgramName("Computer Science");

        // Create a specific date for testing
        LocalDate localDate = LocalDate.of(2023, 1, 1);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        student.setEnrolledAt(date);

        // Act
        StudentDto dto = StudentMapper.toDto(student);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getStudentId());
        assertEquals("John Doe", dto.getName());
        assertEquals("U123456", dto.getStudentFullId());
        assertEquals("Computer Science", dto.getProgramName());
        assertEquals("2023-01-01", dto.getEnrolledAt());
    }

    @Test
    public void testToResponseDto() {
        // Arrange
        Student student = new Student();
        student.setStudentId(1L);
        student.setName("John Doe");
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setStudentFullId("U123456");
        student.setProgramName("Computer Science");

        User user = new User();
        user.setUserId(10);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");

        Role role = new Role();
        role.setRoleId(1);
        role.setRoleName("STUDENT");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        student.setUser(user);

        // Create a specific date for testing
        LocalDate localDate = LocalDate.of(2023, 1, 1);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        student.setEnrolledAt(date);

        // Act
        StudentResponseDto responseDto = StudentMapper.toResponseDto(student);

        // Assert
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getStudentId());
        assertEquals(10, responseDto.getUserId());
        assertEquals("johndoe", responseDto.getUsername());
        assertEquals("john@example.com", responseDto.getEmail());
        assertEquals("John Doe", responseDto.getFullName());
        assertEquals("John", responseDto.getFirstName());
        assertEquals("Doe", responseDto.getLastName());
        assertEquals("1", responseDto.getStudentIdNumber());
        assertEquals("U123456", responseDto.getStudentFullId());
        assertEquals("Computer Science", responseDto.getProgramName());
        assertEquals("2023-01-01", responseDto.getEnrolledAt());
        assertEquals(1, responseDto.getRoles().size());
        assertTrue(responseDto.getRoles().contains("STUDENT"));
    }

    @Test
    public void testUpdateStudentFromDto() {
        // Arrange
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setName("John Doe");
        student.setStudentFullId("U123456");
        student.setProgramName("Computer Science");

        User user = new User();
        user.setEmail("john@example.com");
        student.setUser(user);

        StudentUpdateRequestDto updateDto = new StudentUpdateRequestDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setStudentFullId("U654321");
        updateDto.setProgramName("Mathematics");
        updateDto.setEmail("jane@example.com");

        // Act
        StudentMapper.updateStudentFromDto(student, updateDto);

        // Assert
        assertEquals("Jane", student.getFirstName());
        assertEquals("Smith", student.getLastName());
        assertEquals("Jane Smith", student.getName());
        assertEquals("U654321", student.getStudentFullId());
        assertEquals("Mathematics", student.getProgramName());
        assertEquals("jane@example.com", student.getUser().getEmail());
    }

    @Test
    public void testHandleNulls() {
        // Test null student
        assertNull(StudentMapper.toResponseDto(null));

        // Test null update
        Student student = new Student();
        StudentMapper.updateStudentFromDto(student, null);
        // No exception should be thrown

        // Test null user
        Student studentWithoutUser = new Student();
        studentWithoutUser.setStudentId(1L);
        studentWithoutUser.setName("Test");
        StudentResponseDto dto = StudentMapper.toResponseDto(studentWithoutUser);
        assertNotNull(dto);
        assertNull(dto.getUserId());
        assertNull(dto.getUsername());
        assertNull(dto.getEmail());
        assertNull(dto.getRoles());
    }
}