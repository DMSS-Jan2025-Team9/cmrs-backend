package com.example.usermanagement.validation;

import com.example.usermanagement.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {

    private FirstNameValidator firstNameValidator;
    private LastNameValidator lastNameValidator;
    private StudentValidationChain validationChain;
    private Student student;

    @BeforeEach
    public void setUp() {
        firstNameValidator = new FirstNameValidator();
        lastNameValidator = new LastNameValidator();
        validationChain = new StudentValidationChain()
                .addHandler(firstNameValidator)
                .addHandler(lastNameValidator);

        student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
    }

    @Test
    public void testFirstNameValidator_Success() {
        // Should not throw exception
        assertDoesNotThrow(() -> firstNameValidator.validate(student));
    }

    @Test
    public void testFirstNameValidator_EmptyName() {
        // Set empty first name
        student.setFirstName("");

        // Should throw exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            firstNameValidator.validate(student);
        });

        assertEquals("First Name cannot be empty", exception.getMessage());
    }

    @Test
    public void testFirstNameValidator_NullName() {
        // Set null first name
        student.setFirstName(null);

        // Should throw exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            firstNameValidator.validate(student);
        });

        assertEquals("First Name cannot be empty", exception.getMessage());
    }

    @Test
    public void testLastNameValidator_Success() {
        // Should not throw exception
        assertDoesNotThrow(() -> lastNameValidator.validate(student));
    }

    @Test
    public void testLastNameValidator_EmptyName() {
        // Set empty last name
        student.setLastName("");

        // Should throw exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            lastNameValidator.validate(student);
        });

        assertEquals("Last Name cannot be empty", exception.getMessage());
    }

    @Test
    public void testLastNameValidator_NullName() {
        // Set null last name
        student.setLastName(null);

        // Should throw exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            lastNameValidator.validate(student);
        });

        assertEquals("Last Name cannot be empty", exception.getMessage());
    }

    @Test
    public void testValidationChain_Success() {
        // Should not throw exception
        assertDoesNotThrow(() -> validationChain.validate(student));
    }

    @Test
    public void testValidationChain_FailFirstValidator() {
        // Set empty first name
        student.setFirstName("");

        // Should throw exception from first validator
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validationChain.validate(student);
        });

        assertEquals("First Name cannot be empty", exception.getMessage());
    }

    @Test
    public void testValidationChain_FailSecondValidator() {
        // Set empty last name
        student.setLastName("");

        // Should throw exception from second validator
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validationChain.validate(student);
        });

        assertEquals("Last Name cannot be empty", exception.getMessage());
    }

    @Test
    public void testAddMultipleValidators() {
        // Create a chain with multiple validators
        StudentValidationChain chain = new StudentValidationChain()
                .addHandler(new FirstNameValidator())
                .addHandler(new LastNameValidator());

        // Verify chain works with valid student
        assertDoesNotThrow(() -> chain.validate(student));

        // Verify first validator is checked
        student.setFirstName("");
        assertThrows(IllegalArgumentException.class, () -> chain.validate(student));
    }
}