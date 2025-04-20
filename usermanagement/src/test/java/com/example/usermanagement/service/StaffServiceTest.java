package com.example.usermanagement.service;

import com.example.usermanagement.dto.PasswordUpdateDto;
import com.example.usermanagement.dto.StaffDto;
import com.example.usermanagement.dto.StaffResponseDto;
import com.example.usermanagement.dto.StaffUpdateRequestDto;
import com.example.usermanagement.mapper.StaffMapper;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.StaffRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.impl.StaffServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StaffServiceImpl staffService;

    private Staff staff;
    private User user;
    private Role role;
    private StaffDto staffDto;
    private StaffUpdateRequestDto updateRequestDto;
    private PasswordUpdateDto passwordUpdateDto;

    @BeforeEach
    void setUp() {
        // Setup user
        user = new User();
        user.setUserId(1);
        user.setUsername("staff001");
        user.setEmail("staff001@example.com");
        user.setPassword("encodedPassword");

        // Setup role
        role = new Role();
        role.setRoleId(1);
        role.setRoleName("ROLE_staff");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        // Setup staff
        staff = new Staff();
        staff.setStaffId(1);
        staff.setUser(user);
        staff.setName("John Doe");
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setStaffFullId("S001");
        staff.setDepartment("IT");
        staff.setPosition("Developer");

        // Setup StaffDto
        staffDto = new StaffDto();
        staffDto.setFirstName("John");
        staffDto.setLastName("Doe");
        staffDto.setStaffFullId("S001");
        staffDto.setDepartment("IT");
        staffDto.setPosition("Developer");

        // Setup StaffUpdateRequestDto
        updateRequestDto = new StaffUpdateRequestDto();
        updateRequestDto.setFirstName("John");
        updateRequestDto.setLastName("Doe");
        updateRequestDto.setEmail("john.doe@example.com");
        updateRequestDto.setDepartment("HR");
        updateRequestDto.setPosition("Manager");
        updateRequestDto.setRoles(Arrays.asList("ROLE_staff", "ROLE_admin"));

        // Setup PasswordUpdateDto
        passwordUpdateDto = new PasswordUpdateDto();
        passwordUpdateDto.setCurrentPassword("oldPassword");
        passwordUpdateDto.setNewPassword("newPassword");
        passwordUpdateDto.setConfirmPassword("newPassword");
    }

    @Test
    void testGetAllStaff() {
        // Arrange
        List<Staff> staffList = Arrays.asList(staff);
        when(staffRepository.findAll()).thenReturn(staffList);

        // Act
        List<Staff> result = staffService.getAllStaff();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(staff.getStaffId(), result.get(0).getStaffId());
        verify(staffRepository, times(1)).findAll();
    }

    @Test
    void testGetStaffByUserId() {
        // Arrange
        when(staffRepository.findByUser_UserId(anyInt())).thenReturn(Optional.of(staff));

        // Act
        Staff result = staffService.getStaffByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(staff.getStaffId(), result.getStaffId());
        verify(staffRepository, times(1)).findByUser_UserId(1);
    }

    @Test
    void testGetStaffByUserIdNotFound() {
        // Arrange
        when(staffRepository.findByUser_UserId(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            staffService.getStaffByUserId(999);
        });

        assertTrue(exception.getMessage().contains("Staff not found with user id: 999"));
        verify(staffRepository, times(1)).findByUser_UserId(999);
    }

    @Test
    void testGetStaffResponseByUserId() {
        // Arrange
        when(staffRepository.findByUser_UserId(anyInt())).thenReturn(Optional.of(staff));

        // Act
        StaffResponseDto result = staffService.getStaffResponseByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(staff.getStaffId(), result.getStaffId());
        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(staff.getName(), result.getFullName());
        assertEquals(staff.getDepartment(), result.getDepartment());
        assertEquals(staff.getPosition(), result.getPosition());
        verify(staffRepository, times(1)).findByUser_UserId(1);
    }

    @Test
    void testUpdateStaff() {
        // Arrange
        when(staffRepository.findById(anyLong())).thenReturn(Optional.of(staff));
        when(staffRepository.save(any(Staff.class))).thenReturn(staff);

        StaffDto updatedStaffDto = new StaffDto();
        updatedStaffDto.setFirstName("Updated");
        updatedStaffDto.setLastName("Name");
        updatedStaffDto.setDepartment("Finance");
        updatedStaffDto.setPosition("Director");
        updatedStaffDto.setStaffFullId("S002");

        // Act
        Staff result = staffService.updateStaff(1L, updatedStaffDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("Finance", result.getDepartment());
        assertEquals("Director", result.getPosition());
        assertEquals("S002", result.getStaffFullId());
        verify(staffRepository, times(1)).findById(1L);
        verify(staffRepository, times(1)).save(any(Staff.class));
    }

    @Test
    void testUpdateStaffWithRoles() {
        // Arrange
        when(staffRepository.findByUser_UserId(anyInt())).thenReturn(Optional.of(staff));
        when(roleRepository.findByRoleName("ROLE_staff")).thenReturn(Optional.of(role));

        Role adminRole = new Role();
        adminRole.setRoleId(2);
        adminRole.setRoleName("ROLE_admin");
        when(roleRepository.findByRoleName("ROLE_admin")).thenReturn(Optional.of(adminRole));

        when(staffRepository.save(any(Staff.class))).thenReturn(staff);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        StaffResponseDto result = staffService.updateStaff(1, updateRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("HR", result.getDepartment());
        assertEquals("Manager", result.getPosition());
        verify(staffRepository, times(1)).findByUser_UserId(1);
        verify(roleRepository, times(1)).findByRoleName("ROLE_staff");
        verify(roleRepository, times(1)).findByRoleName("ROLE_admin");
        verify(staffRepository, times(1)).save(any(Staff.class));
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
        boolean result = staffService.updatePassword(1, passwordUpdateDto);

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
        boolean result = staffService.updatePassword(1, passwordUpdateDto);

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
        when(userRepository.findByUserId(anyInt())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        PasswordUpdateDto mismatchPasswordDto = new PasswordUpdateDto();
        mismatchPasswordDto.setCurrentPassword("oldPassword");
        mismatchPasswordDto.setNewPassword("newPassword");
        mismatchPasswordDto.setConfirmPassword("differentPassword");

        // Act
        boolean result = staffService.updatePassword(1, mismatchPasswordDto);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByUserId(1);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteStaff() {
        // Arrange
        when(staffRepository.deleteByUser_UserId(anyInt())).thenReturn(Optional.empty());

        // Act
        staffService.deleteStaff(1);

        // Assert
        verify(staffRepository, times(1)).deleteByUser_UserId(1);
    }

    @Test
    void testGetAllStaffResponses() {
        // Arrange
        List<Staff> staffList = Arrays.asList(staff);
        when(staffRepository.findAll()).thenReturn(staffList);

        // Act
        List<StaffResponseDto> result = staffService.getAllStaffResponses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(staff.getStaffId(), result.get(0).getStaffId());
        assertEquals(user.getUserId(), result.get(0).getUserId());
        assertEquals(staff.getName(), result.get(0).getFullName());
        verify(staffRepository, times(1)).findAll();
    }
}