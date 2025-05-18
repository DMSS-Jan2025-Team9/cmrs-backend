package com.example.usermanagement.security;

import com.example.usermanagement.model.Permission;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private User user;
    private Role role1;
    private Role role2;
    private Permission permission1;
    private Permission permission2;
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        // Set JWT properties
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
                "daf66e01593f61a15b857cf433aae03a005812b31234e149036bcc8dee755dbb");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationDate", 604800000L);

        // Set up user
        user = new User();
        user.setUserId(1);
        user.setUsername("testuser");

        // Set up permissions
        permission1 = new Permission();
        permission1.setPermissionId(1);
        permission1.setPermissionName("read");

        permission2 = new Permission();
        permission2.setPermissionId(2);
        permission2.setPermissionName("write");

        // Set up roles with permissions
        role1 = new Role();
        role1.setRoleId(1);
        role1.setRoleName("admin");
        Set<Permission> adminPermissions = new HashSet<>();
        adminPermissions.add(permission1);
        adminPermissions.add(permission2);
        role1.setPermissions(adminPermissions);

        role2 = new Role();
        role2.setRoleId(2);
        role2.setRoleName("user");
        Set<Permission> userPermissions = new HashSet<>();
        userPermissions.add(permission1);
        role2.setPermissions(userPermissions);

        // Assign roles to user
        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        user.setRoles(roles);

        // Mock authentication
        authentication = new UsernamePasswordAuthenticationToken("testuser", null);
    }

    @Test
    public void testGenerateToken() {
        // Mock repository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Generate token
        String token = jwtTokenProvider.generateToken(authentication);

        // Verify
        assertNotNull(token);
        verify(userRepository).findByUsername("testuser");

        // Verify token structure
        assertTrue(token.length() > 0);
    }

    @Test
    public void testGetUsername() {
        // Mock repository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Generate token
        String token = jwtTokenProvider.generateToken(authentication);

        // Extract username
        String username = jwtTokenProvider.getUsername(token);

        // Verify
        assertEquals("testuser", username);
    }

    @Test
    public void testGetRoles() {
        // Mock repository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Generate token
        String token = jwtTokenProvider.generateToken(authentication);

        // Extract roles
        List<String> roles = jwtTokenProvider.getRoles(token);

        // Verify
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("admin"));
        assertTrue(roles.contains("user"));
    }

    @Test
    public void testGetPermissions() {
        // Mock repository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Generate token
        String token = jwtTokenProvider.generateToken(authentication);

        // Extract permissions
        List<String> permissions = jwtTokenProvider.getPermissions(token);

        // Verify
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertTrue(permissions.contains("read"));
        assertTrue(permissions.contains("write"));
    }

    @Test
    public void testValidateToken() {
        // Mock repository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Generate token
        String token = jwtTokenProvider.generateToken(authentication);

        // Validate token
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Verify
        assertTrue(isValid);
    }

    @Test
    public void testValidateInvalidToken() {
        // Create invalid token
        String invalidToken = "invalid.token.string";

        // Validate token
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Verify
        assertFalse(isValid);
    }
}