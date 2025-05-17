package com.example.usermanagement.service;

import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.repository.RoleRepository;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
    }

    public Student saveStudent(Student student) {
        // Save the student to the database and return the updated entity (with
        // generated studentId)
        return studentRepository.save(student);
    }

    @Retryable(value = { CannotAcquireLockException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User saveUserWithRole(User user, Role role) {
        roleRepository.save(role); // First save the role
        return userRepository.save(user); // Then save the user
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
