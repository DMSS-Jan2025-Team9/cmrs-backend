package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private Student student1;
    private Student student2;
    private StudentResponseDto studentResponseDto1;
    private StudentUpdateRequestDto updateRequestDto;
    private PasswordUpdateDto passwordUpdateDto;

    @BeforeEach
    public void setUp() {
        // Set up test students
        student1 = new Student();
        student1.setStudentId(1L);
        student1.setName("John Doe");
        student1.setStudentFullId("S1001");
        student1.setProgramName("Computer Science");

        student2 = new Student();
        student2.setStudentId(2L);
        student2.setName("Jane Smith");
        student2.setStudentFullId("S1002");
        student2.setProgramName("Data Science");

        // Set up DTOs
        studentResponseDto1 = new StudentResponseDto();
        studentResponseDto1.setStudentId(1L);
        studentResponseDto1.setFullName("John Doe");
        studentResponseDto1.setFirstName("John");
        studentResponseDto1.setLastName("Doe");
        studentResponseDto1.setStudentFullId("S1001");
        studentResponseDto1.setProgramName("Computer Science");

        updateRequestDto = new StudentUpdateRequestDto();
        updateRequestDto.setFirstName("John");
        updateRequestDto.setLastName("Updated");
        updateRequestDto.setProgramName("Updated Program");

        passwordUpdateDto = new PasswordUpdateDto();
        passwordUpdateDto.setCurrentPassword("oldPassword");
        passwordUpdateDto.setNewPassword("newPassword");
    }

    @Test
    public void testGetStudents() {
        // Setup mock
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        // Execute
        List<StudentDto> result = studentController.getStudents();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    public void testGetStudent() {
        // Setup mock
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        // Execute
        ResponseEntity<StudentDto> response = studentController.getStudent(1L);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals("Computer Science", response.getBody().getProgramName());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetStudentNotFound() {
        // Setup mock
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute
        ResponseEntity<StudentDto> response = studentController.getStudent(999L);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    public void testGetStudentByStudentFullId() {
        // Setup mock
        when(studentRepository.findBystudentFullId("S1001")).thenReturn(Optional.of(student1));

        // Execute
        ResponseEntity<StudentDto> response = studentController.getStudentByStudentFullId("S1001");

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        verify(studentRepository, times(1)).findBystudentFullId("S1001");
    }

    @Test
    public void testGetStudentsByProgram() {
        // Setup mock
        when(studentService.findStudentsByProgram("Computer Science")).thenReturn(Arrays.asList(new StudentDto()));

        // Execute
        List<StudentDto> result = studentController.getStudentsByProgram("Computer Science");

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentService, times(1)).findStudentsByProgram("Computer Science");
    }

    @Test
    public void testGetSecureStudentById() {
        // Setup mock
        when(studentService.getStudentResponseByUserId(1)).thenReturn(studentResponseDto1);

        // Execute
        ResponseEntity<StudentResponseDto> response = studentController.getSecureStudentById(1);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getFullName());
        verify(studentService, times(1)).getStudentResponseByUserId(1);
    }

    @Test
    public void testUpdateStudentWithRoles() {
        // Setup mock
        when(studentService.updateStudent(1, updateRequestDto)).thenReturn(studentResponseDto1);

        // Execute
        ResponseEntity<StudentResponseDto> response = studentController.updateStudentWithRoles(1, updateRequestDto);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(studentService, times(1)).updateStudent(1, updateRequestDto);
    }

    @Test
    public void testUpdatePasswordSuccess() {
        // Setup mock
        when(studentService.updatePassword(1, passwordUpdateDto)).thenReturn(true);

        // Execute
        ResponseEntity<ApiResponse> response = studentController.updatePassword(1, passwordUpdateDto);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Password updated successfully", response.getBody().getMessage());
        verify(studentService, times(1)).updatePassword(1, passwordUpdateDto);
    }

    @Test
    public void testUpdatePasswordFailure() {
        // Setup mock
        when(studentService.updatePassword(1, passwordUpdateDto)).thenReturn(false);

        // Execute
        ResponseEntity<ApiResponse> response = studentController.updatePassword(1, passwordUpdateDto);

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Failed to update password. Please check your current password.", response.getBody().getMessage());
        verify(studentService, times(1)).updatePassword(1, passwordUpdateDto);
    }

    @Test
    public void testDeleteStudent() {
        // Execute
        ResponseEntity<ApiResponse> response = studentController.deleteStudent(1);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Student deleted successfully", response.getBody().getMessage());
        verify(studentService, times(1)).deleteStudent(1);
    }
}