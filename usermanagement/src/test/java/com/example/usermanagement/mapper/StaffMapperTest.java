package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.StaffResponseDto;
import com.example.usermanagement.dto.StaffUpdateRequestDto;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.model.User;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StaffMapperTest {

    @Test
    public void testToResponseDto() {
        // Arrange
        Staff staff = new Staff();
        staff.setStaffId(1);
        staff.setName("John Doe");
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setStaffFullId("S123456");
        staff.setDepartment("IT");
        staff.setPosition("Manager");

        User user = new User();
        user.setUserId(10);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");

        Role role = new Role();
        role.setRoleId(1);
        role.setRoleName("STAFF");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        staff.setUser(user);

        // Act
        StaffResponseDto responseDto = StaffMapper.toResponseDto(staff);

        // Assert
        assertNotNull(responseDto);
        assertEquals(1, responseDto.getStaffId());
        assertEquals(10, responseDto.getUserId());
        assertEquals("johndoe", responseDto.getUsername());
        assertEquals("john@example.com", responseDto.getEmail());
        assertEquals("John Doe", responseDto.getFullName());
        assertEquals("John", responseDto.getFirstName());
        assertEquals("Doe", responseDto.getLastName());
        assertEquals("S123456", responseDto.getStaffFullId());
        assertEquals("IT", responseDto.getDepartment());
        assertEquals("Manager", responseDto.getPosition());
        assertEquals(1, responseDto.getRoles().size());
        assertTrue(responseDto.getRoles().contains("STAFF"));
    }

    @Test
    public void testUpdateStaffFromDto() {
        // Arrange
        Staff staff = new Staff();
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setName("John Doe");
        staff.setStaffFullId("S123456");
        staff.setDepartment("IT");
        staff.setPosition("Developer");

        User user = new User();
        user.setEmail("john@example.com");
        staff.setUser(user);

        StaffUpdateRequestDto updateDto = new StaffUpdateRequestDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setStaffFullId("S654321");
        updateDto.setDepartment("HR");
        updateDto.setPosition("Manager");
        updateDto.setEmail("jane@example.com");

        // Act
        StaffMapper.updateStaffFromDto(staff, updateDto);

        // Assert
        assertEquals("Jane", staff.getFirstName());
        assertEquals("Smith", staff.getLastName());
        assertEquals("Jane Smith", staff.getName());
        assertEquals("S654321", staff.getStaffFullId());
        assertEquals("HR", staff.getDepartment());
        assertEquals("Manager", staff.getPosition());
        assertEquals("jane@example.com", staff.getUser().getEmail());
    }

    @Test
    public void testPartialUpdateStaffFromDto() {
        // Arrange
        Staff staff = new Staff();
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setName("John Doe");
        staff.setStaffFullId("S123456");
        staff.setDepartment("IT");
        staff.setPosition("Developer");

        User user = new User();
        user.setEmail("john@example.com");
        staff.setUser(user);

        // Only update some fields
        StaffUpdateRequestDto updateDto = new StaffUpdateRequestDto();
        updateDto.setDepartment("Engineering");
        updateDto.setPosition("Senior Developer");

        // Act
        StaffMapper.updateStaffFromDto(staff, updateDto);

        // Assert
        assertEquals("John", staff.getFirstName()); // Unchanged
        assertEquals("Doe", staff.getLastName()); // Unchanged
        assertEquals("John Doe", staff.getName()); // Unchanged
        assertEquals("S123456", staff.getStaffFullId()); // Unchanged
        assertEquals("Engineering", staff.getDepartment()); // Changed
        assertEquals("Senior Developer", staff.getPosition()); // Changed
        assertEquals("john@example.com", staff.getUser().getEmail()); // Unchanged
    }

    @Test
    public void testHandleNulls() {
        // Test null staff
        assertNull(StaffMapper.toResponseDto(null));

        // Test null update
        Staff staff = new Staff();
        StaffMapper.updateStaffFromDto(staff, null);
        // No exception should be thrown

        // Test null user
        Staff staffWithoutUser = new Staff();
        staffWithoutUser.setStaffId(1);
        staffWithoutUser.setName("Test");
        StaffResponseDto dto = StaffMapper.toResponseDto(staffWithoutUser);
        assertNotNull(dto);
        assertNull(dto.getUserId());
        assertNull(dto.getUsername());
        assertNull(dto.getEmail());
        assertNull(dto.getRoles());
    }
}