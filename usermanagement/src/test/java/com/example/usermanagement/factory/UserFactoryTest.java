package com.example.usermanagement.factory;

import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UserFactoryTest {

    @Test
    public void testCreateUser() {
        // Create a student
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");

        // Create a user from the student
        User user = UserFactory.createUser(student);

        // Verify user properties
        assertNotNull(user);

        // Username should be generated with timestamp suffix
        assertTrue(user.getUsername().startsWith("john.doe"));

        // Password should be hashed
        assertNotNull(user.getPassword());
        assertTrue(user.getPassword().length() > 20); // BCrypt passwords are long

        // Email should be generated
        assertTrue(user.getEmail().startsWith("john.doe"));
        assertTrue(user.getEmail().endsWith("@university.edu"));

        // Creation dates should be set
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    public void testPasswordHashing() {
        // Create two students
        Student student1 = new Student();
        student1.setFirstName("John");
        student1.setLastName("Doe");

        Student student2 = new Student();
        student2.setFirstName("Jane");
        student2.setLastName("Smith");

        // Create users
        User user1 = UserFactory.createUser(student1);
        User user2 = UserFactory.createUser(student2);

        // Verify that passwords are hashed differently even for the same initial
        // password
        assertNotEquals(user1.getPassword(), user2.getPassword());

        // Verify that the password can be verified with BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("defaultpassword", user1.getPassword()));
        assertTrue(encoder.matches("defaultpassword", user2.getPassword()));
    }

    @Test
    public void testUniqueUsernames() {
        // Create two students with the same name
        Student student1 = new Student();
        student1.setFirstName("John");
        student1.setLastName("Doe");

        Student student2 = new Student();
        student2.setFirstName("John");
        student2.setLastName("Doe");

        // Create users
        User user1 = UserFactory.createUser(student1);

        // Small delay to ensure different timestamp
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }

        User user2 = UserFactory.createUser(student2);

        // Verify that usernames are different
        assertNotEquals(user1.getUsername(), user2.getUsername());
        assertNotEquals(user1.getEmail(), user2.getEmail());
    }
}