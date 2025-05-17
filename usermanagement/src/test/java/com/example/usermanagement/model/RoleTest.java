package com.example.usermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleTest {

    private Role role;
    private User user1;
    private User user2;
    private Permission permission1;
    private Permission permission2;

    @BeforeEach
    public void setUp() {
        role = new Role();

        user1 = new User();
        user1.setUserId(1);
        user1.setUsername("user1");

        user2 = new User();
        user2.setUserId(2);
        user2.setUsername("user2");

        permission1 = new Permission();
        permission1.setPermissionId(1);
        permission1.setPermissionName("read");

        permission2 = new Permission();
        permission2.setPermissionId(2);
        permission2.setPermissionName("write");
    }

    @Test
    public void testGettersAndSetters() {
        // Set values
        Integer roleId = 1;
        String roleName = "admin";
        String description = "Administrator role";

        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission1);
        permissions.add(permission2);

        // Use setters
        role.setRoleId(roleId);
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setUsers(users);
        role.setPermissions(permissions);

        // Verify with getters
        assertEquals(roleId, role.getRoleId());
        assertEquals(roleName, role.getRoleName());
        assertEquals(description, role.getDescription());
        assertEquals(users, role.getUsers());
        assertEquals(permissions, role.getPermissions());

        // Verify collections
        assertEquals(2, role.getUsers().size());
        assertTrue(role.getUsers().contains(user1));
        assertTrue(role.getUsers().contains(user2));

        assertEquals(2, role.getPermissions().size());
        assertTrue(role.getPermissions().contains(permission1));
        assertTrue(role.getPermissions().contains(permission2));
    }
}