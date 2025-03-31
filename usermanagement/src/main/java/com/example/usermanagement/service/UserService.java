package com.example.usermanagement.service;

import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {


    @Autowired
    private StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Student saveStudent(Student student) {
        // Save the student to the database and return the updated entity (with generated studentId)
        return studentRepository.save(student);  // Assuming you have a StudentRepository to persist the student
    }

    public User createAndSaveUser(Student student, Role studentRole) {
        User user = new User();

        // Create the username (first name + last name)
        user.setUsername(student.getFirstName().toLowerCase() + student.getLastName().toLowerCase());
        user.setPassword(passwordEncoder.encode("defaultpassword"));

        // Create the email with student ID (first name + last name + student ID)
        String email = student.getStudentFullId() + "@university.edu";

        user.setEmail(email);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        //user.setRoles(Collections.singleton(studentRole));

        return userRepository.save(user);
    }
}

