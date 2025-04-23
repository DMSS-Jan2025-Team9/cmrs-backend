package com.example.usermanagement.factory;

import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Date;

public class UserFactory {

    // Create a user based on the student's information and role
    public static User createUser(Student student) {
        User user = new User();

        // Generate a username (e.g., "john.doe")
        //user.setUsername(student.getFirstName().toLowerCase() + "." + student.getLastName().toLowerCase());

        String base = student.getFirstName().toLowerCase() + "." + student.getLastName().toLowerCase();
        String uniqueSuffix = String.valueOf(System.currentTimeMillis() % 100000); // Just 5-digit timestamp
        user.setUsername(base + uniqueSuffix);

        // Generate a hashed default password
        user.setPassword(hashPassword("defaultpassword"));

        // Generate email based on student name
        user.setEmail(student.getFirstName().toLowerCase() + "." + student.getLastName().toLowerCase() + uniqueSuffix+ "@university.edu");

        // Set the creation and update date
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        return user; // Just create and return the user, don't save here
    }

    // Helper method to hash the password
    private static String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}
