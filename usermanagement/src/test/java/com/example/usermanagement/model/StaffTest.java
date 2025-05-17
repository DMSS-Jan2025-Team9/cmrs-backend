package com.example.usermanagement.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StaffTest {

    @Test
    public void testStaffProperties() {
        // Arrange
        Staff staff = new Staff();
        User user = new User();

        // Act
        staff.setStaffId(1);
        staff.setAdminId(1); // Test the duplicate setter
        staff.setName("John Doe");
        staff.setFirstName("John");
        staff.setLastName("Doe");
        staff.setStaffFullId("S123456");
        staff.setDepartment("IT");
        staff.setPosition("Manager");
        staff.setUser(user);

        // Assert
        assertEquals(1, staff.getStaffId());
        assertEquals("John Doe", staff.getName());
        assertEquals("John", staff.getFirstName());
        assertEquals("Doe", staff.getLastName());
        assertEquals("S123456", staff.getStaffFullId());
        assertEquals("IT", staff.getDepartment());
        assertEquals("Manager", staff.getPosition());
        assertEquals(user, staff.getUser());
    }

    @Test
    public void testUserAssociation() {
        // Arrange
        Staff staff = new Staff();
        User user = new User();
        user.setUserId(100);
        user.setUsername("johndoe");

        // Act
        staff.setUser(user);

        // Assert
        assertNotNull(staff.getUser());
        assertEquals(100, staff.getUser().getUserId());
        assertEquals("johndoe", staff.getUser().getUsername());
    }
}