package com.example.usermanagement.factory;

import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Date;
import java.util.Set;

public class UserFactory {

//    public static User createUser(Student student, Role studentRole) {
//        User user = new User();
//
//        // Generate a username (e.g., "john.doe")
//        user.setUsername(student.getFirstName().toLowerCase() + "." + student.getLastName().toLowerCase());
//
//        // Generate a hashed default password
//        user.setPassword(hashPassword("defaultpassword"));
//
//        // Generate email based on student name
//        user.setEmail(student.getFirstName().toLowerCase() + "." + student.getLastName().toLowerCase() + "@example.com");
//
//        user.setCreatedAt(new Date());
//        user.setUpdatedAt(new Date());
//
//        // Assign the student role
//        user.setRoles(Set.of(studentRole));
//
//        return user;
//    }
//
//    private static String hashPassword(String password) {
//        return new BCryptPasswordEncoder().encode(password);
//    }

    // Create a user based on the student's information and role
    public static User createUser(Student student, Role studentRole) {
        User user = new User();

        // Generate a username (e.g., "john.doe")
        user.setUsername(student.getFirstName().toLowerCase() + "." + student.getLastName().toLowerCase());

        // Generate a hashed default password
        user.setPassword(hashPassword("defaultpassword"));

        // Generate email based on student name
        user.setEmail(student.getFirstName().toLowerCase() + "." + student.getLastName().toLowerCase() + "@university.edu");

        // Set the creation and update date
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        // Assign the student role
        user.setRoles(Set.of(studentRole));

        return user; // Just create and return the user, don't save here
    }

    // Helper method to hash the password
    private static String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}
