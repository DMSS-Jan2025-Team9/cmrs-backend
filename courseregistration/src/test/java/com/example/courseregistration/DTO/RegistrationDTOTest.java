package com.example.courseregistration.DTO;
import com.example.courseregistration.dto.RegistrationDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationDTOTest {

    @Test
    void noArgConstructor_defaultsToNullAndUnset() {
        RegistrationDTO dto = new RegistrationDTO();

        assertNull(dto.getRegistrationId(), "registrationId should be null by default");
        assertNull(dto.getStudentId(), "studentId should be null by default");
        assertNull(dto.getClassId(), "classId should be null by default");
        assertNull(dto.getRegisteredAt(), "registeredAt should be null by default");
        assertNull(dto.getRegistrationStatus(), "registrationStatus should be null by default");
        assertNull(dto.getGroupRegistrationId(), "groupRegistrationId should be null by default");
    }

    @Test
    void allArgsConstructor_initializesAllFields() {
        LocalDateTime now = LocalDateTime.of(2025, 5, 2, 10, 30);
        RegistrationDTO dto = new RegistrationDTO(
                123L,
                456L,
                789L,
                now,
                "Registered",
                42L
        );

        assertEquals(123L, dto.getRegistrationId());
        assertEquals(456L, dto.getStudentId());
        assertEquals(789L, dto.getClassId());
        assertEquals(now, dto.getRegisteredAt());
        assertEquals("Registered", dto.getRegistrationStatus());
        assertEquals(42L, dto.getGroupRegistrationId());
    }

    @Test
    void setRegistrationId_updatesIdOnly() {
        LocalDateTime now = LocalDateTime.now();
        RegistrationDTO dto = new RegistrationDTO(
                1L,
                2L,
                3L,
                now,
                "Waitlisted",
                4L
        );

        // Verify initial id
        assertEquals(1L, dto.getRegistrationId());

        // Update registrationId
        dto.setRegistrationId(999L);
        assertEquals(999L, dto.getRegistrationId(), "setRegistrationId should update the registrationId");

        // Other fields should remain unchanged
        assertEquals(2L, dto.getStudentId());
        assertEquals(3L, dto.getClassId());
        assertEquals(now, dto.getRegisteredAt());
        assertEquals("Waitlisted", dto.getRegistrationStatus());
        assertEquals(4L, dto.getGroupRegistrationId());
    }
}
