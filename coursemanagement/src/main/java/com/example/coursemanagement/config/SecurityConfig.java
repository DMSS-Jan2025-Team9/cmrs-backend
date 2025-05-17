package com.example.coursemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.coursemanagement.security.JwtAuthenticationEntryPoint;
import com.example.coursemanagement.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthEntryPoint, JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection as we are using stateless JWT tokens for
                // authentication
                // This is safe because JWT is sent in Authorization header and not using
                // cookies
                // for session management, so it's not vulnerable to CSRF attacks
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        // Public access endpoints - read operations
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/classSchedule/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/program/**").permitAll()
                        // Admin and staff only - create/update/delete operations
                        .requestMatchers(HttpMethod.POST, "/api/courses/**").hasRole("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/api/classSchedule/**").hasRole("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/classSchedule/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/classSchedule/**").hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/api/program/**").hasRole("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/program/**").hasRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/program/**").hasRole("admin")
                        // Test endpoints
                        .requestMatchers("/api/test/authenticated").authenticated()
                        .requestMatchers("/api/test/admin").hasRole("admin")
                        .requestMatchers("/api/test/staff").hasRole("admin")
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}