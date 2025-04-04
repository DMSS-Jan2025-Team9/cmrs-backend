package com.example.usermanagement.service;

import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserService {


    @Autowired
    private StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public Student saveStudent(Student student) {
        // Save the student to the database and return the updated entity (with generated studentId)
        return studentRepository.save(student);  // Assuming you have a StudentRepository to persist the student
    }

    // Create and save the user for the given student
    public void saveUser(User user) {
        userRepository.save(user); // Save the created user to the database
    }

    @Transactional
    public User saveUserWithRole(User user, Role role) {
        roleRepository.save(role);  // First save the role
        return userRepository.save(user);  // Then save the user
    }

//    public void saveUser(User user, Role role) {
//        // Fetch the student role (role_id = 2)
//        Role studentRole = roleRepository.findById(2L).orElseThrow(() -> new RuntimeException("Student role not found"));
//
//        // Assign role to the user
//        user.setRoles(Set.of(studentRole));
//
//        // Save user to database
//        userRepository.save(user);
//    }
//
//    public User createAndSaveUser(Student student, Role studentRole) {
//        User user = new User();
//
//        // Create the username (first name + last name)
//        user.setUsername(student.getFirstName().toLowerCase() + student.getLastName().toLowerCase());
//        user.setPassword(passwordEncoder.encode("defaultpassword"));
//
//        // Create the email with student ID (first name + last name + student ID)
//        String email = student.getStudentFullId() + "@university.edu";
//
//        user.setEmail(email);
//        user.setCreatedAt(new Date());
//        user.setUpdatedAt(new Date());
//        //user.setRoles(Collections.singleton(studentRole));
//
//        return userRepository.save(user);
//    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

}

