package com.example.usermanagement.model;

import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {

    @Test
    public void testStudentProperties() {
        // Arrange
        Student student = new Student();
        User user = new User();
        Date enrollmentDate = new Date();

        // Act
        student.setStudentId(1L);
        student.setStudentFullId("U123456");
        student.setName("John Doe");
        student.setProgramId(100L);
        student.setEnrolledAt(enrollmentDate);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setProgramName("Computer Science");
        student.setJobId("job123");
        student.setUser(user);

        // Assert
        assertEquals(1L, student.getStudentId());
        assertEquals("U123456", student.getStudentFullId());
        assertEquals("John Doe", student.getName());
        assertEquals(100L, student.getProgramId());
        assertEquals(enrollmentDate, student.getEnrolledAt());
        assertEquals("John", student.getFirstName());
        assertEquals("Doe", student.getLastName());
        assertEquals("Computer Science", student.getProgramName());
        assertEquals("job123", student.getJobId());
        assertEquals(user, student.getUser());
    }

    @Test
    public void testToString() {
        // Arrange
        Student student = new Student();
        student.setProgramId(100L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setName("John Doe");
        student.setStudentFullId("U123456");
        student.setEnrolledAt(new Date());

        // Act
        String result = student.toString();

        // Assert
        assertTrue(result.contains("programId='100'"));
        assertTrue(result.contains("firstName='John'"));
        assertTrue(result.contains("lastName='Doe'"));
        assertTrue(result.contains("name='John Doe'"));
        assertTrue(result.contains("studentId='U123456'"));
    }
}