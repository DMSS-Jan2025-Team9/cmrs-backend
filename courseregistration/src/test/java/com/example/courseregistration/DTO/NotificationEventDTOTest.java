package com.example.courseregistration.DTO;
import com.example.courseregistration.dto.NotificationEventDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationEventDTOTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        NotificationEventDTO dto = new NotificationEventDTO();

        dto.setStudentFullId("SF-001");
        assertEquals("SF-001", dto.getStudentFullId());

        dto.setStudentId(1L);
        assertEquals(1L, dto.getStudentId());

        dto.setClassId(100L);
        assertEquals(100L, dto.getClassId());

        dto.setCourseCode("CS101");
        assertEquals("CS101", dto.getCourseCode());

        dto.setCourseName("Intro to CS");
        assertEquals("Intro to CS", dto.getCourseName());

        dto.setMessage("Welcome!");
        assertEquals("Welcome!", dto.getMessage());

        dto.setEventType("ENROLL");
        assertEquals("ENROLL", dto.getEventType());
    }

    @Test
    void testAllArgsConstructor() {
        NotificationEventDTO dto = new NotificationEventDTO(
                "SF-002", 2L, 200L, "MA202", "Calculus II", 
                "Class full", "WAITLIST"
        );

        assertEquals("SF-002", dto.getStudentFullId());
        assertEquals(2L, dto.getStudentId());
        assertEquals(200L, dto.getClassId());
        assertEquals("MA202", dto.getCourseCode());
        assertEquals("Calculus II", dto.getCourseName());
        assertEquals("Class full", dto.getMessage());
        assertEquals("WAITLIST", dto.getEventType());
    }

    @Test
    void testEqualsAndHashCode() {
        NotificationEventDTO a = new NotificationEventDTO(
                "SF-003", 3L, 300L, "PH301", "Physics III", 
                "Room changed", "UPDATE"
        );
        NotificationEventDTO b = new NotificationEventDTO(
                "SF-003", 3L, 300L, "PH301", "Physics III", 
                "Room changed", "UPDATE"
        );

        assertEquals(a, b, "DTOs with same data should be equal");
        assertEquals(a.hashCode(), b.hashCode(), "Equal DTOs must have same hashCode");
    }

    @Test
    void testToStringContainsAllFields() {
        NotificationEventDTO dto = new NotificationEventDTO(
                "SF-004", 4L, 400L, "CH101", "Chemistry I", 
                "Lab canceled", "CANCEL"
        );

        String str = dto.toString();
        assertTrue(str.contains("studentFullId=SF-004"));
        assertTrue(str.contains("studentId=4"));
        assertTrue(str.contains("classId=400"));
        assertTrue(str.contains("courseCode=CH101"));
        assertTrue(str.contains("courseName=Chemistry I"));
        assertTrue(str.contains("message=Lab canceled"));
        assertTrue(str.contains("eventType=CANCEL"));
    }
}
