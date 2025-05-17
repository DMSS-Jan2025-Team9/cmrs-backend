package com.example.usermanagement.service;

import com.example.usermanagement.dto.JwtAuthResponse;
import com.example.usermanagement.dto.LoginDto;
import com.example.usermanagement.dto.StaffRegistrationDto;
import com.example.usermanagement.dto.StudentRegistrationDto;
import com.example.usermanagement.dto.UserRegistrationDto;
import com.example.usermanagement.factory.UserRegistrationFactory;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.StaffRepository;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.security.JwtTokenProvider;
import com.example.usermanagement.strategy.CompositeNameCleaningStrategy;
import com.example.usermanagement.strategy.StaffEmailStrategy;
import com.example.usermanagement.strategy.StudentEmailStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ActiveProfiles("test")
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserRegistrationFactory userRegistrationFactory;

    @Mock
    private StudentEmailStrategy studentEmailStrategy;

    @Mock
    private StaffEmailStrategy staffEmailStrategy;

    @Mock
    private CompositeNameCleaningStrategy compositeNameCleaningStrategy;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setup() {
        // Always return an encoded password regardless of the input
        doReturn("encodedPassword").when(passwordEncoder).encode(any());
    }

    @Test
    public void testLoginSuccess() {
        // Prepare test data
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("test.jwt.token");

        // Perform the test
        JwtAuthResponse response = authService.login(loginDto);

        // Verify results
        assertNotNull(response);
        assertEquals("test.jwt.token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    public void testRegisterStudent() {
        // Prepare test data
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setRole(Arrays.asList("ROLE_student"));

        StudentRegistrationDto studentRegistrationDto = new StudentRegistrationDto();
        studentRegistrationDto.setFirstName("John");
        studentRegistrationDto.setLastName("Doe");
        studentRegistrationDto.setProgramInfo("1 Computer Science");

        // Mock role repository
        Role studentRole = new Role();
        studentRole.setRoleId(1);
        studentRole.setRoleName("ROLE_student");
        List<Role> roles = new ArrayList<>();
        roles.add(studentRole);
        when(roleRepository.findByRoleNameIn(Arrays.asList("ROLE_student"))).thenReturn(roles);

        // Mock user repository
        User savedUser = new User();
        savedUser.setUserId(1);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Mock student ID and email generation
        when(userRegistrationFactory.generateStudentId(1)).thenReturn("S10001");
        when(studentEmailStrategy.generateEmail("S10001")).thenReturn("s10001@example.com");

        // Mock name cleaning
        when(compositeNameCleaningStrategy.clean("John")).thenReturn("John");
        when(compositeNameCleaningStrategy.clean("Doe")).thenReturn("Doe");

        // Perform the test
        String result = authService.registerStudent(userRegistrationDto, studentRegistrationDto);

        // Verify results
        assertNotNull(result);
        assertEquals("Student registered successfully with ID: S10001", result);
    }

    @Test
    public void testRegisterStaff() {
        // Prepare test data
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setRole(Arrays.asList("ROLE_staff"));

        StaffRegistrationDto staffRegistrationDto = new StaffRegistrationDto();
        staffRegistrationDto.setFirstName("Jane");
        staffRegistrationDto.setLastName("Smith");
        staffRegistrationDto.setDepartment("IT");
        staffRegistrationDto.setPosition("Developer");

        // Mock role repository
        Role staffRole = new Role();
        staffRole.setRoleId(2);
        staffRole.setRoleName("ROLE_staff");
        List<Role> roles = new ArrayList<>();
        roles.add(staffRole);
        when(roleRepository.findByRoleNameIn(Arrays.asList("ROLE_staff"))).thenReturn(roles);

        // Mock user repository
        User savedUser = new User();
        savedUser.setUserId(1);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Mock staff ID and email generation
        when(userRegistrationFactory.generateStaffId(1)).thenReturn("F10001");
        when(staffEmailStrategy.generateEmail("F10001")).thenReturn("f10001@example.com");

        // Mock name cleaning
        when(compositeNameCleaningStrategy.clean("Jane")).thenReturn("Jane");
        when(compositeNameCleaningStrategy.clean("Smith")).thenReturn("Smith");

        // Perform the test
        String result = authService.registerStaff(userRegistrationDto, staffRegistrationDto);

        // Verify results
        assertNotNull(result);
        assertEquals("Staff registered successfully with ID: F10001", result);
    }
}