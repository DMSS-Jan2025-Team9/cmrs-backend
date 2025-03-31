package com.example.usermanagement.factory;

import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Date;

public class UserFactory {

    public static User createUser(Student student, Role studentRole) {
        User user = new User();
        user.setUsername(student.getFirstName().toLowerCase() + student.getLastName().toLowerCase());
        user.setPassword(hashPassword("defaultpassword")); // Hash the password!
        user.setEmail(student.getFirstName().toLowerCase() + "." + student.getLastName().toLowerCase() + "@example.com");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        // Assign role
        //user.setRoles(Collections.singleton(studentRole));

        return user;
    }

    private static String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}
