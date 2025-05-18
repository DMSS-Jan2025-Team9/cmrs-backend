package com.example.usermanagement.service;

import com.example.usermanagement.model.Permission;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;
    private Role role;
    private Permission permission;

    @BeforeEach
    public void setup() {
        // Setup user with roles and permissions
        user = new User();
        user.setUserId(1);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        permission = new Permission();
        permission.setPermissionId(1);
        permission.setPermissionName("READ_COURSES");
        permission.setDescription("Can read courses");

        role = new Role();
        role.setRoleId(1);
        role.setRoleName("STUDENT");
        role.setDescription("Student role");
        role.setPermissions(Set.of(permission));

        user.setRoles(Set.of(role));
    }

    @Test
    public void testLoadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("READ_COURSES")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonExistentUser");
        });

        assertTrue(exception.getMessage().contains("User not found with username: nonExistentUser"));
    }
}