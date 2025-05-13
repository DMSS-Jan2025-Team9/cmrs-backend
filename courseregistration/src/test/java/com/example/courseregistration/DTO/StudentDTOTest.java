package com.example.courseregistration.DTO;
import com.example.courseregistration.dto.StudentDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentDTOTest {

    @Test
    void testGettersAndSetters() {
        StudentDTO student = new StudentDTO();

        student.setName("Alice");
        assertEquals("Alice", student.getName());

        student.setStudentId(123L);
        assertEquals(123L, student.getStudentId());

        student.setStudentFullId("S-123");
        assertEquals("S-123", student.getStudentFullId());

        student.setProgramName("Computer Science");
        assertEquals("Computer Science", student.getProgramName());

        student.setEnrolledAt("2025-05-10");
        assertEquals("2025-05-10", student.getEnrolledAt());
    }

    @Test
    void testEqualsAndHashCode() {
        StudentDTO s1 = new StudentDTO();
        s1.setName("Bob");
        s1.setStudentId(456L);
        s1.setStudentFullId("S-456");
        s1.setProgramName("Mathematics");
        s1.setEnrolledAt("2025-05-11");

        StudentDTO s2 = new StudentDTO();
        s2.setName("Bob");
        s2.setStudentId(456L);
        s2.setStudentFullId("S-456");
        s2.setProgramName("Mathematics");
        s2.setEnrolledAt("2025-05-11");

        // equality
        assertEquals(s1, s2);
        // consistent hashCode
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testToStringContainsAllFields() {
        StudentDTO s = new StudentDTO();
        s.setName("Carol");
        s.setStudentId(789L);
        s.setStudentFullId("S-789");
        s.setProgramName("Physics");
        s.setEnrolledAt("2025-05-12");

        String str = s.toString();
        assertTrue(str.contains("name=Carol"));
        assertTrue(str.contains("studentId=789"));
        assertTrue(str.contains("studentFullId=S-789"));
        assertTrue(str.contains("programName=Physics"));
        assertTrue(str.contains("enrolledAt=2025-05-12"));
    }
}
