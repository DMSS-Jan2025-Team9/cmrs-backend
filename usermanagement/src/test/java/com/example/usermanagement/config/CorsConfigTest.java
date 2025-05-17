package com.example.usermanagement.config;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CorsConfigTest {

    @Test
    public void testCorsConfigurer() {
        // Arrange
        CorsConfig corsConfig = new CorsConfig();

        // Act
        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        // Assert
        assertNotNull(configurer, "WebMvcConfigurer should not be null");
    }

    @Test
    public void testCorsMappingsConfiguration() {
        // Arrange
        CorsConfig corsConfig = new CorsConfig();
        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        // Mock CorsRegistry and CorsRegistration
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(registration);
        when(registration.allowedOrigins("https://wwww.cmrsapp.site")).thenReturn(registration);
        when(registration.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")).thenReturn(registration);
        when(registration.allowedHeaders("*")).thenReturn(registration);
        when(registration.allowCredentials(false)).thenReturn(registration);

        // Act
        configurer.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping("/**");
        verify(registration).allowedOrigins("https://wwww.cmrsapp.site");
        verify(registration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registration).allowedHeaders("*");
        verify(registration).allowCredentials(false);
    }
}
