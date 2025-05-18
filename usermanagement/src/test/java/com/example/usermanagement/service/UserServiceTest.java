package com.example.usermanagement.service;

import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private Student student;
    private Role role;
    private List<Student> students;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUserId(1);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        role = new Role();
        role.setRoleId(1);
        role.setRoleName("STUDENT");

        student = new Student();
        student.setStudentId(1L);
        student.setName("Test Student");
        student.setUser(user);

        students = new ArrayList<>();
        students.add(student);
    }

    @Test
    public void testSaveStudent() {
        // Arrange
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        Student savedStudent = userService.saveStudent(student);

        // Assert
        assertSame(student, savedStudent);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    public void testSaveUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User savedUser = userService.saveUser(user);

        // Assert
        assertSame(user, savedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSaveUserWithRole() {
        // Arrange
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User savedUser = userService.saveUserWithRole(user, role);

        // Assert
        assertSame(user, savedUser);
        verify(roleRepository, times(1)).save(role);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetAllStudents() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(students);

        // Act
        List<Student> result = userService.getAllStudents();

        // Assert
        assertEquals(1, result.size());
        assertEquals(student, result.get(0));
        verify(studentRepository, times(1)).findAll();
    }
}