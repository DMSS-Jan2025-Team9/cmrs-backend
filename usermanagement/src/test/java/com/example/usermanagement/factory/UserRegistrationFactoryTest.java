package com.example.usermanagement.factory;

import com.example.usermanagement.dto.UserRegistrationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserRegistrationFactoryTest {

    private UserRegistrationFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new UserRegistrationFactory();
    }

    @Test
    public void testCreateRegistrationDto() {
        // Test data
        List<String> roles = Arrays.asList("student", "admin");

        // Create DTO
        UserRegistrationDto dto = factory.createRegistrationDto(roles);

        // Verify
        assertNotNull(dto);
        assertEquals(roles, dto.getRole());
        assertEquals(2, dto.getRole().size());
        assertTrue(dto.getRole().contains("student"));
        assertTrue(dto.getRole().contains("admin"));
    }

    @Test
    public void testGenerateStudentId() {
        // Generate IDs for different student IDs
        String id1 = factory.generateStudentId(1);
        String id2 = factory.generateStudentId(10);
        String id3 = factory.generateStudentId(100);

        // Verify format
        assertTrue(id1.startsWith("U01")); // Padding for single digit
        assertTrue(id2.startsWith("U10")); // No padding for double digit
        assertTrue(id3.startsWith("U100")); // No padding for triple digit

        // All IDs should be different
        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id3);

        // All IDs should start with U and have random digits after prefix
        for (String id : Arrays.asList(id1, id2, id3)) {
            assertTrue(id.matches("U\\d{6,7}")); // 6-7 digits after U
        }
    }

    @Test
    public void testGenerateStaffId() {
        // Generate IDs for different staff IDs
        String id1 = factory.generateStaffId(1);
        String id2 = factory.generateStaffId(10);
        String id3 = factory.generateStaffId(100);

        // Verify format
        assertTrue(id1.startsWith("S01")); // Padding for single digit
        assertTrue(id2.startsWith("S10")); // No padding for double digit
        assertTrue(id3.startsWith("S100")); // No padding for triple digit

        // All IDs should be different
        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id3);

        // All IDs should start with S and have random digits after prefix
        for (String id : Arrays.asList(id1, id2, id3)) {
            assertTrue(id.matches("S\\d{6,7}")); // 6-7 digits after S
        }
    }
}