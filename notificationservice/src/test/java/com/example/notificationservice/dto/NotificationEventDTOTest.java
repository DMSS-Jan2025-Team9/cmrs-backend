package com.example.notificationservice.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationEventDTOTest {

    private NotificationEventDTO dto;

    @BeforeEach
    public void setUp() {
        dto = new NotificationEventDTO();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull(dto);
        assertNull(dto.getStudentFullId());
        assertNull(dto.getStudentId());
        assertNull(dto.getClassId());
        assertNull(dto.getCourseCode());
        assertNull(dto.getCourseName());
        assertNull(dto.getMessage());
        assertNull(dto.getEventType());
    }

    @Test
    public void testParameterizedConstructor() {
        String studentFullId = "student123";
        Long studentId = 456L;
        Long classId = 789L;
        String courseCode = "CS101";
        String courseName = "Introduction to Computer Science";
        String message = "Test notification message";
        String eventType = "WAITLISTED";

        NotificationEventDTO paramDto = new NotificationEventDTO(
                studentFullId, studentId, classId, courseCode, courseName, message, eventType);

        assertEquals(studentFullId, paramDto.getStudentFullId());
        assertEquals(studentId, paramDto.getStudentId());
        assertEquals(classId, paramDto.getClassId());
        assertEquals(courseCode, paramDto.getCourseCode());
        assertEquals(courseName, paramDto.getCourseName());
        assertEquals(message, paramDto.getMessage());
        assertEquals(eventType, paramDto.getEventType());
    }

    @Test
    public void testGettersAndSetters() {
        String studentFullId = "student123";
        Long studentId = 456L;
        Long classId = 789L;
        String courseCode = "CS101";
        String courseName = "Introduction to Computer Science";
        String message = "Test notification message";
        String eventType = "WAITLISTED";

        dto.setStudentFullId(studentFullId);
        dto.setStudentId(studentId);
        dto.setClassId(classId);
        dto.setCourseCode(courseCode);
        dto.setCourseName(courseName);
        dto.setMessage(message);
        dto.setEventType(eventType);

        assertEquals(studentFullId, dto.getStudentFullId());
        assertEquals(studentId, dto.getStudentId());
        assertEquals(classId, dto.getClassId());
        assertEquals(courseCode, dto.getCourseCode());
        assertEquals(courseName, dto.getCourseName());
        assertEquals(message, dto.getMessage());
        assertEquals(eventType, dto.getEventType());
    }

    @Test
    public void testToString() {
        String studentFullId = "student123";
        Long studentId = 456L;
        Long classId = 789L;
        String courseCode = "CS101";
        String courseName = "Introduction to Computer Science";
        String message = "This is a test notification message that is longer than 50 characters to test the toString truncation";
        String eventType = "WAITLISTED";

        dto.setStudentFullId(studentFullId);
        dto.setStudentId(studentId);
        dto.setClassId(classId);
        dto.setCourseCode(courseCode);
        dto.setCourseName(courseName);
        dto.setMessage(message);
        dto.setEventType(eventType);

        String toString = dto.toString();

        assertTrue(toString.contains("studentFullId='" + studentFullId + "'"));
        assertTrue(toString.contains("studentId=" + studentId));
        assertTrue(toString.contains("classId=" + classId));
        assertTrue(toString.contains("courseCode='" + courseCode + "'"));
        assertTrue(toString.contains("courseName='" + courseName + "'"));
        assertTrue(toString.contains("This is a test notification message that is long"));
        assertTrue(toString.contains("..."));
        assertTrue(toString.contains("eventType='" + eventType + "'"));
    }

    @Test
    public void testToStringWithNullMessage() {
        dto.setStudentFullId("student123");
        dto.setMessage(null);

        String toString = dto.toString();

        assertTrue(toString.contains("message='null'"));
    }

    @Test
    public void testToStringWithShortMessage() {
        dto.setStudentFullId("student123");
        dto.setMessage("Short message");

        String toString = dto.toString();

        assertTrue(toString.contains("Short message"));
    }
}