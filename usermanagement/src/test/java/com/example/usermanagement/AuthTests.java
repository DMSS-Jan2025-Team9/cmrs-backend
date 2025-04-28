package com.example.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.usermanagement.controller.AuthController;
import com.example.usermanagement.dto.JwtAuthResponse;
import com.example.usermanagement.dto.LoginDto;
import com.example.usermanagement.dto.StudentRegistrationDto;
import com.example.usermanagement.dto.StudentUserRegistrationDto;
import com.example.usermanagement.dto.StaffRegistrationDto;
import com.example.usermanagement.dto.StaffUserRegistrationDto;
import com.example.usermanagement.dto.UserRegistrationDto;
import com.example.usermanagement.service.AuthService;

@SpringBootTest
@ActiveProfiles("test")
public class AuthTests {

    @Autowired
    private AuthController authController;

    @MockitoBean
    private AuthService authService;

    @Test
    public void contextLoads() {
        assertThat(authController).isNotNull();
    }

    @Test
    public void testLogin() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        JwtAuthResponse expectedResponse = new JwtAuthResponse();
        expectedResponse.setAccessToken("test-token");

        when(authService.login(any(LoginDto.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<JwtAuthResponse> response = authController.login(loginDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAccessToken()).isEqualTo("test-token");
    }

    @Test
    public void testRegisterStudent() {
        // Arrange
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setRole(Arrays.asList("ROLE_student"));

        StudentRegistrationDto studentDto = new StudentRegistrationDto();
        studentDto.setFirstName("John");
        studentDto.setLastName("Doe");
        studentDto.setProgramInfo("1 Computer Science");

        StudentUserRegistrationDto registrationDto = new StudentUserRegistrationDto();
        registrationDto.setUser(userDto);
        registrationDto.setStudent(studentDto);

        String expectedMessage = "Student registered successfully";
        when(authService.registerStudent(any(UserRegistrationDto.class), any(StudentRegistrationDto.class)))
                .thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = authController.registerStudent(registrationDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedMessage);
    }

    @Test
    public void testRegisterStaff() {
        // Arrange
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setRole(Arrays.asList("ROLE_staff"));

        StaffRegistrationDto staffDto = new StaffRegistrationDto();
        staffDto.setFirstName("Jane");
        staffDto.setLastName("Smith");
        staffDto.setDepartment("IT");
        staffDto.setPosition("Developer");

        StaffUserRegistrationDto registrationDto = new StaffUserRegistrationDto();
        registrationDto.setUser(userDto);
        registrationDto.setStaff(staffDto);

        String expectedMessage = "Staff registered successfully";
        when(authService.registerStaff(any(UserRegistrationDto.class), any(StaffRegistrationDto.class)))
                .thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = authController.registerStaff(registrationDto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedMessage);
    }
}