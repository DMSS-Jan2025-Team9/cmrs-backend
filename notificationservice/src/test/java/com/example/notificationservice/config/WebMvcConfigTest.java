package com.example.notificationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WebMvcConfigTest {

    @Autowired
    private WebMvcConfig webMvcConfig;

    @Test
    public void contextLoads() {
        assertNotNull(webMvcConfig);
    }

    @Test
    public void testAddResourceHandlers() {
        // Create mock objects
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);

        // Set up the mocks
        when(registry.addResourceHandler(anyString())).thenReturn(registration);

        // Call the method to test
        webMvcConfig.addResourceHandlers(registry);

        // Verify the correct methods were called
        verify(registry).addResourceHandler("/**");
        verify(registration).addResourceLocations("classpath:/static/");
    }
}