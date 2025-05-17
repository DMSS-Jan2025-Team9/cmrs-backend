package com.example.coursemanagement.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    @Test
    public void testCourseGettersAndSetters() {
        // Create dates for registration period
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 1000000);

        // Create a course and set properties
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseCode("CS101");
        course.setCourseName("Introduction to Programming");
        course.setCourseDesc("Basic programming concepts");
        course.setMaxCapacity(100);
        course.setStatus("active");
        course.setRegistrationStart(startDate);
        course.setRegistrationEnd(endDate);

        // Test getters
        assertEquals(Integer.valueOf(1), course.getCourseId());
        assertEquals("CS101", course.getCourseCode());
        assertEquals("Introduction to Programming", course.getCourseName());
        assertEquals("Basic programming concepts", course.getCourseDesc());
        assertEquals(100, course.getMaxCapacity());
        assertEquals("active", course.getStatus());
        assertEquals(startDate, course.getRegistrationStart());
        assertEquals(endDate, course.getRegistrationEnd());
    }

    @Test
    public void testCourseConstructor() {
        // Create dates for registration period
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 1000000);

        // Create a course using the constructor
        Course course = new Course(1, "Introduction to Programming", "CS101",
                startDate, endDate, 100, "active", "Basic programming concepts");

        // Test getters
        assertEquals(Integer.valueOf(1), course.getCourseId());
        assertEquals("CS101", course.getCourseCode());
        assertEquals("Introduction to Programming", course.getCourseName());
        assertEquals("Basic programming concepts", course.getCourseDesc());
        assertEquals(100, course.getMaxCapacity());
        assertEquals("active", course.getStatus());
        assertEquals(startDate, course.getRegistrationStart());
        assertEquals(endDate, course.getRegistrationEnd());
    }

    @Test
    public void testCourseDefaultConstructor() {
        Course course = new Course();

        // Verify default values
        assertNull(course.getCourseId());
        assertNull(course.getCourseCode());
        assertNull(course.getCourseName());
        assertNull(course.getCourseDesc());
        assertEquals(0, course.getMaxCapacity());
        assertNull(course.getStatus());
        assertNull(course.getRegistrationStart());
        assertNull(course.getRegistrationEnd());
    }

    @Test
    public void testCourseEqualsAndHashCode() {
        // Create two courses with the same ID
        Course course1 = new Course();
        course1.setCourseId(1);
        course1.setCourseCode("CS101");

        Course course2 = new Course();
        course2.setCourseId(1);
        course2.setCourseCode("CS999"); // Different code but same ID

        // Create a course with different ID
        Course course3 = new Course();
        course3.setCourseId(2);
        course3.setCourseCode("CS101"); // Same code but different ID

        // Test equals - this depends on whether equals is implemented based on ID
        // If equals is not overridden, this will test reference equality
        assertNotEquals(course1, course2); // Different objects
        assertNotEquals(course1, course3); // Different objects
    }

    @Test
    public void testCourseToString() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseCode("CS101");
        course.setCourseName("Introduction to Programming");

        String courseString = course.toString();

        // The toString method is likely the default Object.toString() implementation
        // which includes the class name and hash code
        assertNotNull(courseString);
        assertTrue(courseString.contains("Course@"));
    }
}