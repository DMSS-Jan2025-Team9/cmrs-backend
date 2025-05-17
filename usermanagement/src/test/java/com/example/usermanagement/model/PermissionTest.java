package com.example.usermanagement.model;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PermissionTest {

    @Test
    public void testPermissionProperties() {
        // Arrange
        Permission permission = new Permission();
        Set<Role> roles = new HashSet<>();
        Role role1 = new Role();
        role1.setRoleId(1);
        role1.setRoleName("ADMIN");
        roles.add(role1);

        // Act
        permission.setPermissionId(1);
        permission.setPermissionName("READ_DATA");
        permission.setDescription("Allows reading data from system");
        permission.setRoles(roles);

        // Assert
        assertEquals(1, permission.getPermissionId());
        assertEquals("READ_DATA", permission.getPermissionName());
        assertEquals("Allows reading data from system", permission.getDescription());
        assertEquals(roles, permission.getRoles());
        assertEquals(1, permission.getRoles().size());
    }

    @Test
    public void testRolesAssociation() {
        // Arrange
        Permission permission = new Permission();

        // Create two roles
        Role adminRole = new Role();
        adminRole.setRoleId(1);
        adminRole.setRoleName("ADMIN");

        Role userRole = new Role();
        userRole.setRoleId(2);
        userRole.setRoleName("USER");

        // Add roles to a set
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        roles.add(userRole);

        // Act
        permission.setRoles(roles);

        // Assert
        assertNotNull(permission.getRoles());
        assertEquals(2, permission.getRoles().size());
        assertTrue(permission.getRoles().contains(adminRole));
        assertTrue(permission.getRoles().contains(userRole));
    }
}