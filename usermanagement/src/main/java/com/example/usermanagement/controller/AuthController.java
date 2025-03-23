package com.example.usermanagement.controller;

import com.example.usermanagement.dto.JwtAuthResponse;
import com.example.usermanagement.dto.LoginDto;
import com.example.usermanagement.dto.SignUpDto;
import com.example.usermanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        JwtAuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SignUpDto signUpDto) {
        String response = authService.register(signUpDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
