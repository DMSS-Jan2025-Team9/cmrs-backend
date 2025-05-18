package com.example.usermanagement.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CourseTest {

    @Test
    public void testCourseConstructorAndGetters() {
        // Arrange
        Integer courseId = 1;
        String courseName = "Java Programming";
        String courseCode = "JP101";
        Date registrationStart = new Date();
        Date registrationEnd = new Date();
        int maxCapacity = 30;
        String status = "OPEN";
        String courseDesc = "Introduction to Java Programming";

        // Act
        Course course = new Course(courseId, courseName, courseCode,
                registrationStart, registrationEnd, maxCapacity, status, courseDesc);

        // Assert
        assertEquals(courseId, course.getCourseId());
        assertEquals(courseName, course.getCourseName());
        assertEquals(courseCode, course.getCourseCode());
        assertEquals(registrationStart, course.getRegistrationStart());
        assertEquals(registrationEnd, course.getRegistrationEnd());
        assertEquals(maxCapacity, course.getMaxCapacity());
        assertEquals(status, course.getStatus());
        assertEquals(courseDesc, course.getCourseDesc());
    }

    @Test
    public void testDefaultConstructorAndSetters() {
        // Arrange
        Course course = new Course();
        Integer courseId = 2;
        String courseName = "Python Programming";
        String courseCode = "PP201";
        Date registrationStart = new Date();
        Date registrationEnd = new Date();
        int maxCapacity = 25;
        String status = "CLOSED";
        String courseDesc = "Introduction to Python Programming";

        // Act
        course.setCourseId(courseId);
        course.setCourseName(courseName);
        course.setCourseCode(courseCode);
        course.setRegistrationStart(registrationStart);
        course.setRegistrationEnd(registrationEnd);
        course.setMaxCapacity(maxCapacity);
        course.setStatus(status);
        course.setCourseDesc(courseDesc);

        // Assert
        assertEquals(courseId, course.getCourseId());
        assertEquals(courseName, course.getCourseName());
        assertEquals(courseCode, course.getCourseCode());
        assertEquals(registrationStart, course.getRegistrationStart());
        assertEquals(registrationEnd, course.getRegistrationEnd());
        assertEquals(maxCapacity, course.getMaxCapacity());
        assertEquals(status, course.getStatus());
        assertEquals(courseDesc, course.getCourseDesc());
        assertNull(course.getClasses()); // Classes is not set, should be null
    }
}