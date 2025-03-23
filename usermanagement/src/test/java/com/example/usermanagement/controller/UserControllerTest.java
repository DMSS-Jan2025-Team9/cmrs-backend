package com.example.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(UserController.class) 
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
/*
    // Test for /register endpoint
    @Test
    public void testRegisterUser() throws Exception {
        User user = new User("john_doe", "password123");
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User john_doe registered successfully!"));
    }

    // Test for /login endpoint
    @Test
    public void testLoginUser() throws Exception {
        User user = new User("john_doe", "password123");
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Logging in for user: john_doe"));
    } */
}
