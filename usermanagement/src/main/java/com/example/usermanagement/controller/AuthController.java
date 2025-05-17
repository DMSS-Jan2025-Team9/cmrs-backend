package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        JwtAuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/student")
    public ResponseEntity<String> registerStudent(@RequestBody StudentUserRegistrationDto registrationDto) {
        UserRegistrationDto userRegistrationDto = registrationDto.getUser();
        StudentRegistrationDto studentRegistrationDto = registrationDto.getStudent();
        String response = authService.registerStudent(userRegistrationDto, studentRegistrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/register/staff")
    public ResponseEntity<String> registerStaff(@RequestBody StaffUserRegistrationDto registrationDto) {
        UserRegistrationDto userRegistrationDto = registrationDto.getUser();
        StaffRegistrationDto staffRegistrationDto = registrationDto.getStaff();
        String response = authService.registerStaff(userRegistrationDto, staffRegistrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
