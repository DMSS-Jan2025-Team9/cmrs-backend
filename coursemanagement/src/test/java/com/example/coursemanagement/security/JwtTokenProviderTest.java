package com.example.coursemanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private String jwtSecret = "testsecretkeytestsecretkeytestsecretkeytestsecretkeytestsecretkey";
    private Key key;

    // Helper method to create a test token
    private String createTestToken(String username, List<String> roles, List<String> permissions) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .signWith(key)
                .compact();
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", jwtSecret);
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Test
    public void testGetUsername() {
        // Create test token
        String token = createTestToken("testuser", Collections.singletonList("ROLE_USER"), Collections.emptyList());

        // Get username from token
        String username = jwtTokenProvider.getUsername(token);

        // Verify username
        assertEquals("testuser", username);
    }

    @Test
    public void testGetRoles() {
        // Create test token with roles
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        String token = createTestToken("testuser", roles, Collections.emptyList());

        // Get roles from token
        List<String> retrievedRoles = jwtTokenProvider.getRoles(token);

        // Verify roles
        assertEquals(2, retrievedRoles.size());
        assertTrue(retrievedRoles.contains("ROLE_USER"));
        assertTrue(retrievedRoles.contains("ROLE_ADMIN"));
    }

    @Test
    public void testGetPermissions() {
        // Create test token with permissions
        List<String> permissions = Arrays.asList("READ", "WRITE");
        String token = createTestToken("testuser", Collections.emptyList(), permissions);

        // Get permissions from token
        List<String> retrievedPermissions = jwtTokenProvider.getPermissions(token);

        // Verify permissions
        assertEquals(2, retrievedPermissions.size());
        assertTrue(retrievedPermissions.contains("READ"));
        assertTrue(retrievedPermissions.contains("WRITE"));
    }

    @Test
    public void testValidateToken_ValidToken() {
        // Create valid test token
        String token = createTestToken("testuser", Collections.emptyList(), Collections.emptyList());

        // Validate token
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Verify token is valid
        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_InvalidToken() {
        // Test with invalid token
        boolean isValid = jwtTokenProvider.validateToken("invalid.token.string");

        // Verify token is invalid
        assertFalse(isValid);
    }
}