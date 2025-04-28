package com.example.coursemanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        // Public access endpoints - read operations
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/classSchedule/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/program/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/classes/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/classes/**").permitAll()
                        // Admin and staff only - create/update/delete operations
                        .requestMatchers(HttpMethod.PUT, "/api/classes/**").permitAll() // hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/api/courses/**").permitAll()// hasRole("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").permitAll() // hasRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").permitAll() // hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/api/classSchedule/**").permitAll() // hasRole("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/classSchedule/**").permitAll() // hasRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/classSchedule/**").permitAll() // hasRole("admin")
                        .requestMatchers(HttpMethod.POST, "/api/program/**").permitAll() // hasRole("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/program/**").permitAll() // hasRole("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/program/**").permitAll() // hasRole("admin")
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