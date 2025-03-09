package com.example.usermanagement.config;

import com.example.usermanagement.model.Student;
import com.example.usermanagement.strategy.CapitalizeNameStrategy;
import com.example.usermanagement.strategy.CompositeNameCleaningStrategy;
import com.example.usermanagement.strategy.NameCleaningStrategy;
import com.example.usermanagement.strategy.RemoveSpecialCharsStrategy;
import com.example.usermanagement.validation.FirstNameValidator;
import com.example.usermanagement.validation.LastNameValidator;
import com.example.usermanagement.validation.StudentValidationChain;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<Student, Student> {
//    @Override
//    public Student process(Student student) throws Exception {
//        return student;
//    }

//    @Override
//    public Student process(Student student) throws Exception {
//        // Log the student details (for debugging purposes)
//        System.out.println("Processing student: " + student);
//
//        // Perform some validation (example: check if the student has a valid name)
//        if (student.getName() == null || student.getName().isEmpty()) {
//            throw new IllegalArgumentException("Student name cannot be empty");
//        }
//
//        // Example transformation (e.g., capitalize name)
//        String name = student.getName();
//        student.setName(name != null ? name.toUpperCase() : name);
//
//        // You can add more transformations or actions here...
//
//        return student;  // Return the processed student
//    }

    private final StudentValidationChain validationChain;

    private final NameCleaningStrategy nameCleaningStrategy; // Name cleaning strategy

    // Constructor to initialize the validation chain and name cleaning strategy
    public StudentProcessor(NameCleaningStrategy nameCleaningStrategy) {
        // Initialize the validation chain with validators
        this.validationChain = new StudentValidationChain()
                .addHandler(new FirstNameValidator())
                .addHandler(new LastNameValidator());

        // Initialize the name cleaning strategy
        this.nameCleaningStrategy = nameCleaningStrategy;
    }

    @Override
    public Student process(Student student) throws Exception {
        // Log the student details (for debugging)
        System.out.println("Processing student: " + student);

        // Validate using the chain
        validationChain.validate(student);

        // Clean and capitalize first and last names
        if (student.getFirstName() != null) {
            student.setFirstName(nameCleaningStrategy.clean(student.getFirstName())); // Clean and capitalize first name
        }

        if (student.getLastName() != null) {
            student.setLastName(nameCleaningStrategy.clean(student.getLastName())); // Clean and capitalize last name
        }

        // Combine cleaned names if both are present
        if (!student.getFirstName().trim().isEmpty() && !student.getLastName().trim().isEmpty()) {
            student.setName(student.getFirstName().trim() + " " + student.getLastName().trim());
        }

        // Log the student details after processing
        System.out.println("After processing student: " + student);


        return student;
    }
}
