package com.example.usermanagement.config;

import com.example.usermanagement.model.Student;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<Student, Student> {
//    @Override
//    public Student process(Student student) throws Exception {
//        return student;
//    }

    @Override
    public Student process(Student student) throws Exception {
        // Log the student details (for debugging purposes)
        System.out.println("Processing student: " + student);

        // Perform some validation (example: check if the student has a valid name)
        if (student.getName() == null || student.getName().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }

        // Example transformation (e.g., capitalize name)
        String name = student.getName();
        student.setName(name != null ? name.toUpperCase() : name);

        // You can add more transformations or actions here...

        return student;  // Return the processed student
    }
}
