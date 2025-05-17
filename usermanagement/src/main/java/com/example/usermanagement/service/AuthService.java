package com.example.usermanagement.service;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.model.User;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.StaffRepository;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.security.JwtTokenProvider;
import com.example.usermanagement.factory.UserRegistrationFactory;
import com.example.usermanagement.strategy.StudentEmailStrategy;
import com.example.usermanagement.strategy.StaffEmailStrategy;
import com.example.usermanagement.strategy.CompositeNameCleaningStrategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserRegistrationFactory userRegistrationFactory;
    private final StudentEmailStrategy studentEmailStrategy;
    private final StaffEmailStrategy staffEmailStrategy;
    private final CompositeNameCleaningStrategy compositeNameCleaningStrategy;

    @Value("${app.default-temp-password:temp123}")
    private String defaultTempPassword;

    public AuthService(AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            StudentRepository studentRepository,
            StaffRepository staffRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            UserRegistrationFactory userRegistrationFactory,
            StudentEmailStrategy studentEmailStrategy,
            StaffEmailStrategy staffEmailStrategy,
            CompositeNameCleaningStrategy compositeNameCleaningStrategy) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userRegistrationFactory = userRegistrationFactory;
        this.studentEmailStrategy = studentEmailStrategy;
        this.staffEmailStrategy = staffEmailStrategy;
        this.compositeNameCleaningStrategy = compositeNameCleaningStrategy;
    }

    public JwtAuthResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setAccessToken(token);

        return response;
    }

    private User createBaseUser(String tempUsername, String tempEmail) {
        User user = new User();
        user.setUsername(tempUsername);
        user.setEmail(tempEmail);
        user.setPassword(passwordEncoder.encode(defaultTempPassword));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return user;
    }

    private Set<Role> resolveRoles(List<String> roleNames) {
        List<Role> matchedRoles = roleRepository.findByRoleNameIn(roleNames);
        if (matchedRoles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No matching roles found");
        }
        return new HashSet<>(matchedRoles);
    }

    private String cleanFullName(String firstName, String lastName) {
        return compositeNameCleaningStrategy.clean(firstName) + " " + compositeNameCleaningStrategy.clean(lastName);
    }

    public String registerStudent(UserRegistrationDto userRegistrationDto, StudentRegistrationDto studentDto) {
        User user = createBaseUser("Temp Student", "temp_student@mail.com");
        user.setRoles(resolveRoles(userRegistrationDto.getRole()));

        User savedUser = userRepository.save(user);

        String studentFullId = userRegistrationFactory.generateStudentId(savedUser.getUserId());
        String email = studentEmailStrategy.generateEmail(studentFullId);
        savedUser.setUsername(studentFullId);
        savedUser.setEmail(email);
        userRepository.save(savedUser);

        String[] programInfo = studentDto.getProgramInfo().split(" ", 2);
        Long programId = Long.parseLong(programInfo[0]);
        String programName = programInfo[1];

        Student student = new Student();
        student.setUser(savedUser);
        student.setFirstName(compositeNameCleaningStrategy.clean(studentDto.getFirstName()));
        student.setLastName(compositeNameCleaningStrategy.clean(studentDto.getLastName()));
        student.setName(cleanFullName(studentDto.getFirstName(), studentDto.getLastName()));
        student.setProgramId(programId);
        student.setProgramName(programName);
        student.setStudentFullId(studentFullId);
        student.setEnrolledAt(new Date());

        studentRepository.save(student);

        return "Student registered successfully with ID: " + studentFullId;
    }

    public String registerStaff(UserRegistrationDto userRegistrationDto, StaffRegistrationDto staffDto) {
        User user = createBaseUser("Temp Staff", "temp_staff@mail.com");
        user.setRoles(resolveRoles(userRegistrationDto.getRole()));

        User savedUser = userRepository.save(user);

        String staffFullId = userRegistrationFactory.generateStaffId(savedUser.getUserId());
        String email = staffEmailStrategy.generateEmail(staffFullId);
        savedUser.setUsername(staffFullId);
        savedUser.setEmail(email);
        userRepository.save(savedUser);

        Staff staff = new Staff();
        staff.setUser(savedUser);
        staff.setFirstName(compositeNameCleaningStrategy.clean(staffDto.getFirstName()));
        staff.setLastName(compositeNameCleaningStrategy.clean(staffDto.getLastName()));
        staff.setName(cleanFullName(staffDto.getFirstName(), staffDto.getLastName()));
        staff.setDepartment(staffDto.getDepartment());
        staff.setPosition(staffDto.getPosition());
        staff.setStaffFullId(staffFullId);

        staffRepository.save(staff);

        return "Staff registered successfully with ID: " + staffFullId;
    }
}