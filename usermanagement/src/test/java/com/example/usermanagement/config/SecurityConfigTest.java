package com.example.usermanagement.config;

import com.example.usermanagement.security.JwtAuthenticationEntryPoint;
import com.example.usermanagement.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthEntryPoint;

    @MockBean
    private JwtAuthenticationFilter jwtAuthFilter;

    @Test
    public void contextLoads() {
        assertNotNull(securityConfig);
    }

    @Test
    public void testPasswordEncoder() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);

        // Test encoder functionality
        String password = "testPassword";
        String encodedPassword = encoder.encode(password);

        // Verify that the password is encoded differently
        assertTrue(encodedPassword.length() > 0);
        assertTrue(!password.equals(encodedPassword));

        // Verify that the original password still matches the encoded one
        assertTrue(encoder.matches(password, encodedPassword));
    }

    @Test
    public void testAuthenticationManager() throws Exception {
        // Mock the AuthenticationConfiguration
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);

        when(authConfig.getAuthenticationManager()).thenReturn(mockAuthManager);

        // Test the method
        AuthenticationManager authManager = securityConfig.authenticationManager(authConfig);

        assertNotNull(authManager);
    }

    @Test
    public void testSecurityFilterChain() throws Exception {
        // Create a mock HttpSecurity
        HttpSecurity httpSecurity = mock(HttpSecurity.class);

        // Mock method chaining for HttpSecurity
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.exceptionHandling(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);

        // Mock HttpSecurity.build()
        DefaultSecurityFilterChain mockFilterChain = mock(DefaultSecurityFilterChain.class);
        when(httpSecurity.build()).thenReturn(mockFilterChain);

        // Call the method under test
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

        // Verify the filter chain is created
        assertNotNull(filterChain);
        assertSame(mockFilterChain, filterChain);
    }
}