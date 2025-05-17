package com.example.usermanagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OpenApiConfigTest {

    @Autowired
    private OpenApiConfig openApiConfig;

    @Test
    public void contextLoads() {
        assertNotNull(openApiConfig);
    }

    @Test
    public void testCustomOpenAPI() {
        // Arrange
        OpenApiConfig config = new OpenApiConfig();

        // Act
        OpenAPI openAPI = config.customOpenAPI();

        // Assert
        assertNotNull(openAPI, "OpenAPI should not be null");

        // Check components
        Components components = openAPI.getComponents();
        assertNotNull(components, "Components should not be null");

        // Check security scheme
        SecurityScheme securityScheme = components.getSecuritySchemes().get("bearer-jwt");
        assertNotNull(securityScheme, "Security scheme should not be null");
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
        assertEquals(SecurityScheme.In.HEADER, securityScheme.getIn());
        assertEquals("Authorization", securityScheme.getName());

        // Check info
        Info info = openAPI.getInfo();
        assertNotNull(info, "Info should not be null");
        assertEquals("API Documentation", info.getTitle());
        assertEquals("1.0.0", info.getVersion());

        // Check security requirements
        List<SecurityRequirement> securityRequirements = openAPI.getSecurity();
        assertNotNull(securityRequirements, "Security requirements should not be null");
        assertFalse(securityRequirements.isEmpty(), "Security requirements should not be empty");

        SecurityRequirement securityRequirement = securityRequirements.get(0);
        assertNotNull(securityRequirement.get("bearer-jwt"), "Bearer JWT requirement should exist");
        assertTrue(securityRequirement.get("bearer-jwt").contains("read"), "Should contain read scope");
        assertTrue(securityRequirement.get("bearer-jwt").contains("write"), "Should contain write scope");
    }
}