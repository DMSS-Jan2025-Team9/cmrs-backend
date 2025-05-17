package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginDto loginDto;
    private JwtAuthResponse jwtAuthResponse;
    private StudentUserRegistrationDto studentRegDto;
    private StaffUserRegistrationDto staffRegDto;

    @BeforeEach
    public void setUp() {
        // Set up login DTO and response
        loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");

        jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken("test.jwt.token");
        jwtAuthResponse.setTokenType("Bearer");

        // Set up student registration DTO
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setRole(Arrays.asList("student"));

        StudentRegistrationDto studentDto = new StudentRegistrationDto();
        studentDto.setFirstName("John");
        studentDto.setLastName("Doe");
        studentDto.setProgramInfo("Computer Science");

        studentRegDto = new StudentUserRegistrationDto();
        studentRegDto.setUser(userDto);
        studentRegDto.setStudent(studentDto);

        // Set up staff registration DTO
        UserRegistrationDto staffUserDto = new UserRegistrationDto();
        staffUserDto.setRole(Arrays.asList("staff"));

        StaffRegistrationDto staffDto = new StaffRegistrationDto();
        staffDto.setFirstName("Jane");
        staffDto.setLastName("Smith");
        staffDto.setDepartment("IT");

        staffRegDto = new StaffUserRegistrationDto();
        staffRegDto.setUser(staffUserDto);
        staffRegDto.setStaff(staffDto);
    }

    @Test
    public void testLogin() {
        // Setup mock
        when(authService.login(any(LoginDto.class))).thenReturn(jwtAuthResponse);

        // Execute
        ResponseEntity<JwtAuthResponse> response = authController.login(loginDto);

        // Verify
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtAuthResponse, response.getBody());
        verify(authService, times(1)).login(loginDto);
    }

    @Test
    public void testRegisterStudent() {
        // Setup mock
        when(authService.registerStudent(any(UserRegistrationDto.class), any(StudentRegistrationDto.class)))
                .thenReturn("Student registered successfully");

        // Execute
        ResponseEntity<String> response = authController.registerStudent(studentRegDto);

        // Verify
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Student registered successfully", response.getBody());
        verify(authService, times(1)).registerStudent(
                studentRegDto.getUser(),
                studentRegDto.getStudent());
    }

    @Test
    public void testRegisterStaff() {
        // Setup mock
        when(authService.registerStaff(any(UserRegistrationDto.class), any(StaffRegistrationDto.class)))
                .thenReturn("Staff registered successfully");

        // Execute
        ResponseEntity<String> response = authController.registerStaff(staffRegDto);

        // Verify
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Staff registered successfully", response.getBody());
        verify(authService, times(1)).registerStaff(
                staffRegDto.getUser(),
                staffRegDto.getStaff());
    }
}