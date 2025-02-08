package com.example.usermanagement.controller;

import com.example.usermanagement.model.User;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    // TODO: Endpoint to register a new user
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        // For now, just returning a simple success message
        return "User " + user.getUsername() + " registered successfully!";
    }

    // TODO: Endpoint to check user login
    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        // Return a dummy user message for now
        return "Logging in for user: " + user.getUsername();
    }

}