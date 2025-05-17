package com.example.usermanagement.security;

import com.example.usermanagement.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    public void setUp() {
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternal_WithValidToken() throws Exception {
        // Set up mock behavior for a valid token
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(tokenProvider.validateToken("valid.token.here")).thenReturn(true);
        when(tokenProvider.getUsername("valid.token.here")).thenReturn("testuser");

        List<String> roles = Arrays.asList("admin", "user");
        List<String> permissions = Arrays.asList("read", "write");

        when(tokenProvider.getRoles("valid.token.here")).thenReturn(roles);
        when(tokenProvider.getPermissions("valid.token.here")).thenReturn(permissions);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        // Execute the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify interactions
        verify(filterChain).doFilter(request, response);
        verify(tokenProvider).validateToken("valid.token.here");
        verify(tokenProvider).getUsername("valid.token.here");
        verify(tokenProvider).getRoles("valid.token.here");
        verify(tokenProvider).getPermissions("valid.token.here");
        verify(userDetailsService).loadUserByUsername("testuser");

        // Verify that authentication is set in security context
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    public void testDoFilterInternal_WithInvalidToken() throws Exception {
        // Set up mock behavior for an invalid token
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
        when(tokenProvider.validateToken("invalid.token")).thenReturn(false);

        // Execute the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify interactions
        verify(filterChain).doFilter(request, response);
        verify(tokenProvider).validateToken("invalid.token");

        // Verify no further processing happened
        verify(tokenProvider, never()).getUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        // Verify security context is empty
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternal_WithNoToken() throws Exception {
        // Set up mock behavior for no token
        when(request.getHeader("Authorization")).thenReturn(null);

        // Execute the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify interactions
        verify(filterChain).doFilter(request, response);

        // Verify no token processing happened
        verify(tokenProvider, never()).validateToken(anyString());
        verify(tokenProvider, never()).getUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());

        // Verify security context is empty
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternal_WithException() throws Exception {
        // Set up mock behavior to throw exception
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(tokenProvider.validateToken("valid.token.here")).thenReturn(true);
        when(tokenProvider.getUsername("valid.token.here")).thenThrow(new RuntimeException("Test exception"));

        // Execute the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify filter chain continues despite exception
        verify(filterChain).doFilter(request, response);

        // Verify security context is empty due to exception
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}