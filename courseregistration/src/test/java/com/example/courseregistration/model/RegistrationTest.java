package com.example.courseregistration.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationTest {

    @Test
    void testDefaultConstructorInitializesFieldsToNull() {
        Registration reg = new Registration();

        assertNull(reg.getRegistrationId(), "registrationId should be null by default");
        assertNull(reg.getStudentId(), "studentId should be null by default");
        assertNull(reg.getClassId(), "classId should be null by default");
        assertNull(reg.getRegisteredAt(), "registeredAt should be null by default");
        assertNull(reg.getRegistrationStatus(), "registrationStatus should be null by default");
        assertNull(reg.getGroupRegistrationId(), "groupRegistrationId should be null by default");
    }

    @Test
    void testSettersAndGetters() {
        Registration reg = new Registration();

        reg.setRegistrationId(10L);
        assertEquals(10L, reg.getRegistrationId());

        reg.setStudentId(20L);
        assertEquals(20L, reg.getStudentId());

        reg.setClassId(30L);
        assertEquals(30L, reg.getClassId());

        LocalDateTime now = LocalDateTime.of(2025, 5, 10, 14, 30);
        reg.setRegisteredAt(now);
        assertEquals(now, reg.getRegisteredAt());

        reg.setRegistrationStatus("CONFIRMED");
        assertEquals("CONFIRMED", reg.getRegistrationStatus());

        reg.setGroupRegistrationId(40L);
        assertEquals(40L, reg.getGroupRegistrationId());
    }
}
