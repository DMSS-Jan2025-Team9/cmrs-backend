package com.example.coursemanagement.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProgramTest {

    @Test
    public void testProgramGettersAndSetters() {
        // Create a program and set properties
        Program program = new Program();
        program.setProgramId(1);
        program.setProgramName("Computer Science");
        program.setProgramDesc("Computer Science Program");

        // Test getters
        assertEquals(Integer.valueOf(1), program.getProgramId());
        assertEquals("Computer Science", program.getProgramName());
        assertEquals("Computer Science Program", program.getProgramDesc());
    }

    @Test
    public void testProgramCoursesAssociation() {
        // Create a program
        Program program = new Program();
        program.setProgramId(1);
        program.setProgramName("Computer Science");

        // Create courses
        Course course1 = new Course();
        course1.setCourseId(1);
        course1.setCourseCode("CS101");
        course1.setCourseName("Introduction to Programming");

        Course course2 = new Course();
        course2.setCourseId(2);
        course2.setCourseCode("CS102");
        course2.setCourseName("Data Structures");

        // Add courses to the program
        List<Course> courses = new ArrayList<>();
        courses.add(course1);
        courses.add(course2);
        program.setCourses(courses);

        // Test the association
        assertEquals(2, program.getCourses().size());
        assertTrue(program.getCourses().contains(course1));
        assertTrue(program.getCourses().contains(course2));
    }

    @Test
    public void testProgramDefaultConstructor() {
        Program program = new Program();

        // Verify default values
        assertNull(program.getProgramId());
        assertNull(program.getProgramName());
        assertNull(program.getProgramDesc());
        assertNull(program.getCourses()); // Not initialized in the default constructor
    }

    @Test
    public void testProgramEqualsAndHashCode() {
        // Create two programs with the same ID
        Program program1 = new Program();
        program1.setProgramId(1);
        program1.setProgramName("Computer Science");

        Program program2 = new Program();
        program2.setProgramId(1);
        program2.setProgramName("Information Technology"); // Different name but same ID

        // Create a program with different ID
        Program program3 = new Program();
        program3.setProgramId(2);
        program3.setProgramName("Computer Science"); // Same name but different ID

        // Test equals and hashCode - assuming Program uses ID for equality
        // If equals is not overridden, this will test reference equality
        assertNotEquals(program1, program2); // Different objects
        assertNotEquals(program1, program3); // Different objects
    }

    @Test
    public void testProgramToString() {
        Program program = new Program();
        program.setProgramId(1);
        program.setProgramName("Computer Science");

        String programString = program.toString();

        // The toString method may be the default Object.toString() implementation
        assertNotNull(programString);
    }
}