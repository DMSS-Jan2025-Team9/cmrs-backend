package com.example.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;
    private Role role1;
    private Role role2;

    @BeforeEach
    public void setUp() {
        user = new User();

        role1 = new Role();
        role1.setRoleId(1);
        role1.setRoleName("admin");

        role2 = new Role();
        role2.setRoleId(2);
        role2.setRoleName("student");
    }

    @Test
    public void testGettersAndSetters() {
        // Set values
        Integer userId = 1;
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";
        Date createdAt = new Date();
        Date updatedAt = new Date();
        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);

        // Use setters
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);
        user.setRoles(roles);

        // Verify with getters
        assertEquals(userId, user.getUserId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
        assertEquals(roles, user.getRoles());
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(role1));
        assertTrue(user.getRoles().contains(role2));
    }

    @Test
    public void testToString() {
        // Set up user
        user.setUserId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        Date date = new Date();
        user.setCreatedAt(date);
        user.setUpdatedAt(date);

        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        user.setRoles(roles);

        String toString = user.toString();

        // Check contents of toString
        assertTrue(toString.contains("userId=1"));
        assertTrue(toString.contains("username='testuser'"));
        assertTrue(toString.contains("email='test@example.com'"));
        assertTrue(toString.contains("admin"));
        assertTrue(toString.contains("student"));
    }

    @Test
    public void testToStringWithNullRoles() {
        // Set up user without roles
        user.setUserId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRoles(null);

        String toString = user.toString();

        // Check it handles null roles correctly
        assertTrue(toString.contains("No Roles"));
    }
}