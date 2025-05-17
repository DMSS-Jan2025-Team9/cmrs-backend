package com.example.usermanagement.controller;

import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
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
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        // Setup test users
        user1 = new User();
        user1.setUserId(1);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        user2 = new User();
        user2.setUserId(2);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
    }

    @Test
    public void testGetUsers() {
        // Setup mock
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Execute the method
        List<UserDto> result = userController.getUsers();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify the DTOs contain the expected data
        assertEquals(1, result.get(0).getUserId());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user1@example.com", result.get(0).getEmail());

        assertEquals(2, result.get(1).getUserId());
        assertEquals("user2", result.get(1).getUsername());
        assertEquals("user2@example.com", result.get(1).getEmail());

        // Verify the repository method was called
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserFound() {
        // Setup mock
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // Execute the method
        ResponseEntity<UserDto> response = userController.getUser(1L);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getUserId());
        assertEquals("user1", response.getBody().getUsername());
        assertEquals("user1@example.com", response.getBody().getEmail());

        // Verify the repository method was called
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUserNotFound() {
        // Setup mock to return empty
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute the method
        ResponseEntity<UserDto> response = userController.getUser(999L);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verify the repository method was called
        verify(userRepository, times(1)).findById(999L);
    }
}