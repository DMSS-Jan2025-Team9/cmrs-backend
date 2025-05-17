package com.example.notificationservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationTest {

    private Notification notification;
    private Timestamp now;

    @BeforeEach
    public void setUp() {
        notification = new Notification();
        now = Timestamp.from(Instant.now());
    }

    @Test
    public void testGetterAndSetters() {
        // Set values
        Long notificationId = 1L;
        String studentFullId = "student123";
        Long userId = 456L;
        String notificationMessage = "Test notification message";

        // Use setters
        notification.setNotificationId(notificationId);
        notification.setStudentFullId(studentFullId);
        notification.setUserId(userId);
        notification.setNotificationMessage(notificationMessage);
        notification.setCreatedAt(now);
        notification.setSentAt(now);
        notification.setReadAt(now);

        // Verify using getters
        assertEquals(notificationId, notification.getNotificationId());
        assertEquals(studentFullId, notification.getStudentFullId());
        assertEquals(userId, notification.getUserId());
        assertEquals(notificationMessage, notification.getNotificationMessage());
        assertEquals(now, notification.getCreatedAt());
        assertEquals(now, notification.getSentAt());
        assertEquals(now, notification.getReadAt());
    }

    @Test
    public void testToString() {
        // Set values
        notification.setNotificationId(1L);
        notification.setStudentFullId("student123");
        notification.setUserId(456L);
        notification.setNotificationMessage(
                "This is a test notification message that is longer than 50 characters to test the toString truncation");
        notification.setCreatedAt(now);
        notification.setSentAt(now);
        notification.setReadAt(now);

        // Test toString
        String toString = notification.toString();

        // Check the contents
        assertTrue(toString.contains("notificationId=1"));
        assertTrue(toString.contains("studentFullId='student123'"));
        assertTrue(toString.contains("userId=456"));
        assertTrue(toString.contains("This is a test notification message that is long"));
        assertTrue(toString.contains("..."));
    }

    @Test
    public void testToStringWithNullMessage() {
        // Set values with null message
        notification.setNotificationId(1L);
        notification.setStudentFullId("student123");
        notification.setUserId(456L);
        notification.setNotificationMessage(null);

        // Test toString
        String toString = notification.toString();

        // Check that it handles null message
        assertTrue(toString.contains("null"));
    }

    @Test
    public void testToStringWithShortMessage() {
        // Set values with short message
        notification.setNotificationId(1L);
        notification.setStudentFullId("student123");
        notification.setUserId(456L);
        notification.setNotificationMessage("Short message");

        // Test toString
        String toString = notification.toString();

        // Check that it includes the full short message
        assertTrue(toString.contains("Short message..."));
    }
}