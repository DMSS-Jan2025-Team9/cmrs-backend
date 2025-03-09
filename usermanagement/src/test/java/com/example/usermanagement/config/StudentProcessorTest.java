package com.example.usermanagement.config;

import com.example.usermanagement.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentProcessorTest {

    private StudentProcessor processor;

    @BeforeEach
    void setUp() {
        //processor = new StudentProcessor();  // Instantiate the processor
    }

    @Test
    void testProcessValidStudent() throws Exception {
        // Create a valid student
        Student student = new Student();
        student.setName("john doe");

        // Process the student
        Student processedStudent = processor.process(student);

        // Assert the student name is capitalized
        assertNotNull(processedStudent);
        assertEquals("JOHN DOE", processedStudent.getName());
    }

    @Test
    void testProcessStudentWithEmptyName() {
        // Create a student with empty name
        Student student = new Student();
        student.setName("");

        // Assert an exception is thrown
        assertThrows(IllegalArgumentException.class, () -> processor.process(student));
    }

    @Test
    void testProcessStudentWithNullName() {
        // Create a student with null name
        Student student = new Student();
        student.setName(null);

        // Assert an exception is thrown
        assertThrows(IllegalArgumentException.class, () -> processor.process(student));
    }
}
