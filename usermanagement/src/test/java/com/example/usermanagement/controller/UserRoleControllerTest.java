package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.service.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRoleControllerTest {

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private UserRoleController userRoleController;

    @Test
    public void testGetAllUsers() {
        // Arrange
        List<UserRoleResponse> users = Arrays.asList(
                createUserRoleResponse(1, "user1", "user1@example.com"),
                createUserRoleResponse(2, "user2", "user2@example.com"));
        when(userRoleService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Users retrieved successfully", apiResponse.getMessage());
        assertEquals(2, ((List<?>) apiResponse.getData()).size());
    }

    @Test
    public void testGetUserById() {
        // Arrange
        UserRoleResponse user = createUserRoleResponse(1, "user1", "user1@example.com");
        when(userRoleService.getUserById(1)).thenReturn(user);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.getUserById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("User retrieved successfully", apiResponse.getMessage());
        assertEquals(1, ((UserRoleResponse) apiResponse.getData()).getUserId());
    }

    @Test
    public void testGetAllRoles() {
        // Arrange
        List<RoleDto> roles = Arrays.asList(
                createRoleDto(1, "admin", "Admin role"),
                createRoleDto(2, "user", "User role"));
        when(userRoleService.getAllRoles()).thenReturn(roles);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.getAllRoles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Roles retrieved successfully", apiResponse.getMessage());
        assertEquals(2, ((List<?>) apiResponse.getData()).size());
    }

    @Test
    public void testGetRoleById() {
        // Arrange
        RoleDto role = createRoleDto(1, "admin", "Admin role");
        when(userRoleService.getRoleById(1)).thenReturn(role);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.getRoleById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Role retrieved successfully", apiResponse.getMessage());
        assertEquals(1, ((RoleDto) apiResponse.getData()).getRoleId());
    }

    @Test
    public void testGetAllPermissions() {
        // Arrange
        List<PermissionDto> permissions = Arrays.asList(
                createPermissionDto(1, "view_users", "View users"),
                createPermissionDto(2, "manage_users", "Manage users"));
        when(userRoleService.getAllPermissions()).thenReturn(permissions);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.getAllPermissions();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Permissions retrieved successfully", apiResponse.getMessage());
        assertEquals(2, ((List<?>) apiResponse.getData()).size());
    }

    @Test
    public void testGetPermissionById() {
        // Arrange
        PermissionDto permission = createPermissionDto(1, "view_users", "View users");
        when(userRoleService.getPermissionById(1)).thenReturn(permission);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.getPermissionById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Permission retrieved successfully", apiResponse.getMessage());
        assertEquals(1, ((PermissionDto) apiResponse.getData()).getPermissionId());
    }

    @Test
    public void testCreateRole() {
        // Arrange
        RoleDto roleDto = createRoleDto(null, "new_role", "New role");
        RoleDto createdRole = createRoleDto(3, "new_role", "New role");

        when(userRoleService.createRole(any(RoleDto.class))).thenReturn(createdRole);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.createRole(roleDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Role created successfully", apiResponse.getMessage());
        assertEquals(3, ((RoleDto) apiResponse.getData()).getRoleId());
    }

    @Test
    public void testCreatePermission() {
        // Arrange
        PermissionDto permissionDto = createPermissionDto(null, "new_permission", "New permission");
        PermissionDto createdPermission = createPermissionDto(3, "new_permission", "New permission");

        when(userRoleService.createPermission(any(PermissionDto.class))).thenReturn(createdPermission);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.createPermission(permissionDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Permission created successfully", apiResponse.getMessage());
        assertEquals(3, ((PermissionDto) apiResponse.getData()).getPermissionId());
    }

    @Test
    public void testUpdateRolePermissions() {
        // Arrange
        RolePermissionRequest request = new RolePermissionRequest();
        request.setRoleId(1);
        Set<Integer> permissionIds = new HashSet<>(Arrays.asList(1, 2, 3));
        request.setPermissionIds(permissionIds);

        RoleDto updatedRole = createRoleDto(1, "admin", "Admin role");

        when(userRoleService.updateRolePermissions(any(RolePermissionRequest.class))).thenReturn(updatedRole);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.updateRolePermissions(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Role permissions updated successfully", apiResponse.getMessage());
        assertEquals(1, ((RoleDto) apiResponse.getData()).getRoleId());
    }

    @Test
    public void testAssignRolesToUser() {
        // Arrange
        UserRoleRequest request = new UserRoleRequest();
        request.setUserId(1);
        Set<Integer> roleIds = new HashSet<>(Arrays.asList(1, 2));
        request.setRoleIds(roleIds);

        UserRoleResponse updatedUser = createUserRoleResponse(1, "user1", "user1@example.com");

        when(userRoleService.assignRolesToUser(any(UserRoleRequest.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.assignRolesToUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("User roles updated successfully", apiResponse.getMessage());
        assertEquals(1, ((UserRoleResponse) apiResponse.getData()).getUserId());
    }

    @Test
    public void testUpdateRole() {
        // Arrange
        RoleDto roleDto = createRoleDto(1, "updated_role", "Updated role");
        RoleDto updatedRole = createRoleDto(1, "updated_role", "Updated role");

        when(userRoleService.updateRole(eq(1), any(RoleDto.class))).thenReturn(updatedRole);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.updateRole(1, roleDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Role updated successfully", apiResponse.getMessage());
        assertEquals(1, ((RoleDto) apiResponse.getData()).getRoleId());
    }

    @Test
    public void testUpdatePermission() {
        // Arrange
        PermissionDto permissionDto = createPermissionDto(1, "updated_permission", "Updated permission");
        PermissionDto updatedPermission = createPermissionDto(1, "updated_permission", "Updated permission");

        when(userRoleService.updatePermission(eq(1), any(PermissionDto.class))).thenReturn(updatedPermission);

        // Act
        ResponseEntity<ApiResponse> response = userRoleController.updatePermission(1, permissionDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Permission updated successfully", apiResponse.getMessage());
        assertEquals(1, ((PermissionDto) apiResponse.getData()).getPermissionId());
    }

    @Test
    public void testDeleteRole() {
        // Act
        ResponseEntity<ApiResponse> response = userRoleController.deleteRole(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Role deleted successfully", apiResponse.getMessage());
    }

    @Test
    public void testDeletePermission() {
        // Act
        ResponseEntity<ApiResponse> response = userRoleController.deletePermission(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Permission deleted successfully", apiResponse.getMessage());
    }

    // Helper methods
    private UserRoleResponse createUserRoleResponse(Integer userId, String username, String email) {
        UserRoleResponse response = new UserRoleResponse();
        response.setUserId(userId);
        response.setUsername(username);
        response.setEmail(email);
        response.setRoles(new HashSet<>());
        return response;
    }

    private RoleDto createRoleDto(Integer roleId, String roleName, String description) {
        RoleDto role = new RoleDto();
        role.setRoleId(roleId);
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setPermissions(new HashSet<>());
        return role;
    }

    private PermissionDto createPermissionDto(Integer permissionId, String permissionName, String description) {
        PermissionDto permission = new PermissionDto();
        permission.setPermissionId(permissionId);
        permission.setPermissionName(permissionName);
        permission.setDescription(description);
        return permission;
    }
}