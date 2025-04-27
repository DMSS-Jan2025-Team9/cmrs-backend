package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.service.UserRoleService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class UserRoleController {
    
    @Autowired
    private UserRoleService userRoleService;
    
    // Get all users with their roles
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('view_users')")
    @Operation(summary = "Get all users with their roles")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserRoleResponse> users = userRoleService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse(true, "Users retrieved successfully", users));
    }
    
    // Get user by ID
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('view_users')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Integer userId) {
        UserRoleResponse user = userRoleService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse(true, "User retrieved successfully", user));
    }
    
    // Get all roles
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('view_roles')")
    @Operation(summary = "Get all roles")
    public ResponseEntity<ApiResponse> getAllRoles() {
        List<RoleDto> roles = userRoleService.getAllRoles();
        return ResponseEntity.ok(new ApiResponse(true, "Roles retrieved successfully", roles));
    }
    
    // Get role by ID
    @GetMapping("/roles/{roleId}")
    @PreAuthorize("hasAuthority('view_roles')")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable Integer roleId) {
        RoleDto role = userRoleService.getRoleById(roleId);
        return ResponseEntity.ok(new ApiResponse(true, "Role retrieved successfully", role));
    }

    // Get permission by ID
    @GetMapping("/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('view_permissions')")
    @Operation(summary = "Get permission by ID")
    public ResponseEntity<ApiResponse> getPermissionById(@PathVariable Integer permissionId) {
        PermissionDto permission = userRoleService.getPermissionById(permissionId);
        return ResponseEntity.ok(new ApiResponse(true, "Permission retrieved successfully", permission));
    }
    
    // Get all permissions
    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('view_permissions')")
    @Operation(summary = "Get all permissions")
    public ResponseEntity<ApiResponse> getAllPermissions() {
        List<PermissionDto> permissions = userRoleService.getAllPermissions();
        return ResponseEntity.ok(new ApiResponse(true, "Permissions retrieved successfully", permissions));
    }
    
    // Create a new role
    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('manage_roles')")
    @Operation(summary = "Create a new role")
    public ResponseEntity<ApiResponse> createRole(@RequestBody RoleDto roleDto) {
        RoleDto createdRole = userRoleService.createRole(roleDto);
        return new ResponseEntity<>(new ApiResponse(true, "Role created successfully", createdRole), HttpStatus.CREATED);
    }
    
    // Create a new permission
    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('manage_permissions')")
    @Operation(summary = "Create a new permission")
    public ResponseEntity<ApiResponse> createPermission(@RequestBody PermissionDto permissionDto) {
        PermissionDto createdPermission = userRoleService.createPermission(permissionDto);
        return new ResponseEntity<>(new ApiResponse(true, "Permission created successfully", createdPermission), HttpStatus.CREATED);
    }
    
    // Update role permissions
    @PutMapping("/roles/permissions")
    @PreAuthorize("hasAuthority('manage_role_permissions')")
    @Operation(summary = "Update role permissions")
    public ResponseEntity<ApiResponse> updateRolePermissions(@RequestBody RolePermissionRequest request) {
        RoleDto updatedRole = userRoleService.updateRolePermissions(request);
        return ResponseEntity.ok(new ApiResponse(true, "Role permissions updated successfully", updatedRole));
    }
    
    // Assign roles to user
    @PutMapping("/users/roles")
    @PreAuthorize("hasAuthority('manage_user_roles')")
    @Operation(summary = "Assign roles to user")
    public ResponseEntity<ApiResponse> assignRolesToUser(@RequestBody UserRoleRequest request) {
        UserRoleResponse updatedUser = userRoleService.assignRolesToUser(request);
        return ResponseEntity.ok(new ApiResponse(true, "User roles updated successfully", updatedUser));
    }
    
    // Update a role
    @PutMapping("/roles/{roleId}")
    @PreAuthorize("hasAuthority('manage_roles')")
    @Operation(summary = "Update a role")
    public ResponseEntity<ApiResponse> updateRole(@PathVariable Integer roleId, @RequestBody RoleDto roleDto) {
        RoleDto updatedRole = userRoleService.updateRole(roleId, roleDto);
        return ResponseEntity.ok(new ApiResponse(true, "Role updated successfully", updatedRole));
    }
    
    // Update a permission
    @PutMapping("/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('manage_permissions')")
    @Operation(summary = "Update a permission")
    public ResponseEntity<ApiResponse> updatePermission(@PathVariable Integer permissionId, @RequestBody PermissionDto permissionDto) {
        PermissionDto updatedPermission = userRoleService.updatePermission(permissionId, permissionDto);
        return ResponseEntity.ok(new ApiResponse(true, "Permission updated successfully", updatedPermission));
    }
    
    // Delete a role
    @DeleteMapping("/roles/{roleId}")
    @PreAuthorize("hasAuthority('manage_roles')")
    @Operation(summary = "Delete a role")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable Integer roleId) {
        userRoleService.deleteRole(roleId);
        return ResponseEntity.ok(new ApiResponse(true, "Role deleted successfully"));
    }
    
    // Delete a permission
    @DeleteMapping("/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('manage_permissions')")
    @Operation(summary = "Delete a permission")
    public ResponseEntity<ApiResponse> deletePermission(@PathVariable Integer permissionId) {
        userRoleService.deletePermission(permissionId);
        return ResponseEntity.ok(new ApiResponse(true, "Permission deleted successfully"));
    }
}
