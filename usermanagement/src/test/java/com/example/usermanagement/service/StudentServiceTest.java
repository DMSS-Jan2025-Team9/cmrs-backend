package com.example.usermanagement.service;

import com.example.usermanagement.dto.PasswordUpdateDto;
import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.dto.StudentResponseDto;
import com.example.usermanagement.dto.StudentUpdateRequestDto;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private User user;
    private Role role;
    private StudentUpdateRequestDto updateRequestDto;
    private PasswordUpdateDto passwordUpdateDto;

    @BeforeEach
    void setUp() {
        // Setup user
        user = new User();
        user.setUserId(1);
        user.setUsername("student001");
        user.setEmail("student001@example.com");
        user.setPassword("encodedPassword");

        // Setup role
        role = new Role();
        role.setRoleId(1);
        role.setRoleName("ROLE_student");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        // Setup student
        student = new Student();
        student.setStudentId(1L);
        student.setUser(user);
        student.setName("John Doe");
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setStudentFullId("ST001");
        student.setProgramId(1L);
        student.setProgramName("Computer Science");
        student.setEnrolledAt(new Date());

        // Setup StudentUpdateRequestDto
        updateRequestDto = new StudentUpdateRequestDto();
        updateRequestDto.setFirstName("John");
        updateRequestDto.setLastName("Doe");
        updateRequestDto.setEmail("john.doe@example.com");
        updateRequestDto.setStudentFullId("ST001");
        updateRequestDto.setProgramName("Information Technology");
        updateRequestDto.setRoles(Arrays.asList("ROLE_student", "ROLE_admin"));

        // Setup PasswordUpdateDto
        passwordUpdateDto = new PasswordUpdateDto();
        passwordUpdateDto.setCurrentPassword("oldPassword");
        passwordUpdateDto.setNewPassword("newPassword");
        passwordUpdateDto.setConfirmPassword("newPassword");
    }

    @Test
    void testGetAllStudents() {
        // Arrange
        List<Student> studentList = Arrays.asList(student);
        when(studentRepository.findAll()).thenReturn(studentList);

        // Act
        List<Student> result = studentService.getAllStudents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(student.getStudentId(), result.get(0).getStudentId());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetStudentByUserId() {
        // Arrange
        when(studentRepository.findByUser_UserId(anyInt())).thenReturn(Optional.of(student));

        // Act
        Student result = studentService.getStudentByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(student.getStudentId(), result.getStudentId());
        verify(studentRepository, times(1)).findByUser_UserId(1);
    }

    @Test
    void testGetStudentByUserIdNotFound() {
        // Arrange
        when(studentRepository.findByUser_UserId(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            studentService.getStudentByUserId(999);
        });

        assertTrue(exception.getMessage().contains("Student not found with id: 999"));
        verify(studentRepository, times(1)).findByUser_UserId(999);
    }

    @Test
    void testGetStudentResponseByUserId() {
        // Arrange
        when(studentRepository.findByUser_UserId(anyInt())).thenReturn(Optional.of(student));

        // Act
        StudentResponseDto result = studentService.getStudentResponseByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(student.getStudentId(), result.getStudentId());
        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(student.getName(), result.getFullName());
        assertEquals(student.getProgramName(), result.getProgramName());
        verify(studentRepository, times(1)).findByUser_UserId(1);
    }

    @Test
    void testUpdateStudentWithRoles() {
        // Arrange
        when(studentRepository.findByUser_UserId(anyInt())).thenReturn(Optional.of(student));
        when(roleRepository.findByRoleName("ROLE_student")).thenReturn(Optional.of(role));

        Role adminRole = new Role();
        adminRole.setRoleId(2);
        adminRole.setRoleName("ROLE_admin");
        when(roleRepository.findByRoleName("ROLE_admin")).thenReturn(Optional.of(adminRole));

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        StudentResponseDto result = studentService.updateStudent(1, updateRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Information Technology", result.getProgramName());
        verify(studentRepository, times(1)).findByUser_UserId(1);
        verify(roleRepository, times(1)).findByRoleName("ROLE_student");
        verify(roleRepository, times(1)).findByRoleName("ROLE_admin");
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void testUpdatePasswordSuccess() {
        // Arrange
        when(userRepository.findByUserId(anyInt())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        boolean result = studentService.updatePassword(1, passwordUpdateDto);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByUserId(1);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdatePasswordFailCurrentPasswordMismatch() {
        // Arrange
        when(userRepository.findByUserId(anyInt())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act
        boolean result = studentService.updatePassword(1, passwordUpdateDto);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByUserId(1);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdatePasswordFailNewPasswordMismatch() {
        // Arrange
        PasswordUpdateDto mismatchedPasswordDto = new PasswordUpdateDto();
        mismatchedPasswordDto.setCurrentPassword("oldPassword");
        mismatchedPasswordDto.setNewPassword("newPassword");
        mismatchedPasswordDto.setConfirmPassword("differentPassword");

        when(userRepository.findByUserId(anyInt())).thenReturn(Optional.of(user));
        // The implementation actually checks the current password first, so we need to
        // mock this
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        boolean result = studentService.updatePassword(1, mismatchedPasswordDto);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByUserId(1);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteStudent() {
        // Arrange
        // Using when() instead of doNothing() for non-void method
        when(studentRepository.deleteByUser_UserId(anyInt())).thenReturn(Optional.empty());

        // Act
        studentService.deleteStudent(1);

        // Assert
        verify(studentRepository, times(1)).deleteByUser_UserId(1);
    }

    @Test
    void testGetAllStudentResponses() {
        // Arrange
        List<Student> studentList = Arrays.asList(student);
        when(studentRepository.findAll()).thenReturn(studentList);

        // Act
        List<StudentResponseDto> result = studentService.getAllStudentResponses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(student.getStudentId(), result.get(0).getStudentId());
        assertEquals(user.getUserId(), result.get(0).getUserId());
        assertEquals(student.getName(), result.get(0).getFullName());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testFindStudentsByProgram() {
        // Arrange
        List<Student> studentList = Arrays.asList(student);
        when(studentRepository.findByProgramName(anyString())).thenReturn(studentList);

        // Act
        List<StudentDto> result = studentService.findStudentsByProgram("Computer Science");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Computer Science", student.getProgramName());
        verify(studentRepository, times(1)).findByProgramName("Computer Science");
    }
}