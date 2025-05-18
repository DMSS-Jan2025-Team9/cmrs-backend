package com.example.notificationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.mockito.Mockito.*;

class CorsConfigTest {

    @Test
    void corsConfigurer_addsCorrectCorsMappings() {
        // Arrange: mock CorsRegistry and CorsRegistration
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);

        // Stub the fluent API
        when(registry.addMapping("/**")).thenReturn(registration);
        when(registration.allowedOrigins("https://www.cmrsapp.site")).thenReturn(registration);
        when(registration.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS"))
                .thenReturn(registration);
        when(registration.allowedHeaders("*")).thenReturn(registration);
        when(registration.allowCredentials(false)).thenReturn(registration);

        // Act: invoke our configurer
        CorsConfig config = new CorsConfig();
        WebMvcConfigurer configurer = config.corsConfigurer();
        configurer.addCorsMappings(registry);

        // Assert: verify that we added exactly the calls we expect
        verify(registry).addMapping("/**");
        verify(registration).allowedOrigins("https://www.cmrsapp.site");
        verify(registration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registration).allowedHeaders("*");
        verify(registration).allowCredentials(false);

        // And nothing else
        verifyNoMoreInteractions(registry, registration);
    }
}
