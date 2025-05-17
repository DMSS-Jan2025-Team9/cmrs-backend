package com.example.usermanagement.strategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailGenerationStrategyTest {

    @Test
    public void testStudentEmailStrategy() {
        // Arrange
        StudentEmailStrategy strategy = new StudentEmailStrategy();
        String studentId = "U123456";

        // Act
        String email = strategy.generateEmail(studentId);

        // Assert
        assertEquals("U123456@university.edu", email);
    }

    @Test
    public void testStaffEmailStrategy() {
        // Arrange
        StaffEmailStrategy strategy = new StaffEmailStrategy();
        String staffId = "S789012";

        // Act
        String email = strategy.generateEmail(staffId);

        // Assert
        assertEquals("S789012@staff.university.edu", email);
    }

    @Test
    public void testStudentEmailStrategy_WithEmptyId() {
        // Arrange
        StudentEmailStrategy strategy = new StudentEmailStrategy();

        // Act
        String email = strategy.generateEmail("");

        // Assert
        assertEquals("@university.edu", email);
    }

    @Test
    public void testStaffEmailStrategy_WithEmptyId() {
        // Arrange
        StaffEmailStrategy strategy = new StaffEmailStrategy();

        // Act
        String email = strategy.generateEmail("");

        // Assert
        assertEquals("@staff.university.edu", email);
    }
}