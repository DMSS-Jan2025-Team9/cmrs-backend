package com.example.usermanagement.service;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.mapper.PermissionMapper;
import com.example.usermanagement.mapper.RoleMapper;
import com.example.usermanagement.model.*;
import com.example.usermanagement.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public UserRoleService(UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RoleMapper roleMapper,
            PermissionMapper permissionMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    // Get all users with their roles and permissions
    public List<UserRoleResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
    }

    // Get user by ID with roles
    public UserRoleResponse getUserById(Integer userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));
        return mapUserToDto(user);
    }

    // Get all roles with permissions
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::mapRoleToDto)
                .collect(Collectors.toList());
    }

    // Get role by ID
    public RoleDto getRoleById(Integer roleId) {
        Role role = roleRepository.findByRoleId(roleId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with id: " + roleId));
        return roleMapper.mapRoleToDto(role);
    }

    // Get permission by ID
    public PermissionDto getPermissionById(Integer permissionId) {
        Permission permission = permissionRepository.findByPermissionId(permissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Permission not found with id: " + permissionId));
        return permissionMapper.mapPermissionToDto(permission);
    }

    // Get all permissions
    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::mapPermissionToDto)
                .collect(Collectors.toList());
    }

    // Create a new role
    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        // Check if role already exists
        if (roleRepository.existsByRoleName(roleDto.getRoleName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name already exists");
        }

        Role role = new Role();
        role.setRoleName(roleDto.getRoleName());
        role.setDescription(roleDto.getDescription());

        Role savedRole = roleRepository.save(role);

        // If permissions are provided, assign them
        if (roleDto.getPermissions() != null && !roleDto.getPermissions().isEmpty()) {
            Set<Permission> permissions = roleDto.getPermissions().stream()
                    .map(p -> permissionRepository.findByPermissionId(p.getPermissionId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Permission not found with id: " + p.getPermissionId())))
                    .collect(Collectors.toSet());

            savedRole.setPermissions(permissions);
            savedRole = roleRepository.save(savedRole);
        }

        return roleMapper.mapRoleToDto(savedRole);
    }

    // Create a new permission
    @Transactional
    public PermissionDto createPermission(PermissionDto permissionDto) {
        // Check if permission already exists
        if (permissionRepository.existsByPermissionName(permissionDto.getPermissionName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission name already exists");
        }

        Permission permission = new Permission();
        permission.setPermissionName(permissionDto.getPermissionName());
        permission.setDescription(permissionDto.getDescription());

        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.mapPermissionToDto(savedPermission);
    }

    // Update role permissions
    @Transactional
    public RoleDto updateRolePermissions(RolePermissionRequest request) {
        Role role = roleRepository.findByRoleId(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Role not found with id: " + request.getRoleId()));

        Set<Permission> permissions = request.getPermissionIds().stream()
                .map(id -> permissionRepository.findByPermissionId(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Permission not found with id: " + id)))
                .collect(Collectors.toSet());

        role.setPermissions(permissions);
        roleRepository.save(role);

        return roleMapper.mapRoleToDto(role);
    }

    // Assign roles to user
    @Transactional
    public UserRoleResponse assignRolesToUser(UserRoleRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + request.getUserId()));

        Set<Role> roles = request.getRoleIds().stream()
                .map(id -> roleRepository.findByRoleId(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Role not found with id: " + id)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        user.setUpdatedAt(new Date());
        userRepository.save(user);

        return mapUserToDto(user);
    }

    // Update role
    @Transactional
    public RoleDto updateRole(Integer roleId, RoleDto roleDto) {
        Role role = roleRepository.findByRoleId(roleId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with id: " + roleId));

        // If changing role name, check if new name already exists
        if (!role.getRoleName().equals(roleDto.getRoleName()) &&
                roleRepository.existsByRoleName(roleDto.getRoleName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name already exists");
        }

        role.setRoleName(roleDto.getRoleName());
        role.setDescription(roleDto.getDescription());

        roleRepository.save(role);

        return roleMapper.mapRoleToDto(role);
    }

    // Update permission
    @Transactional
    public PermissionDto updatePermission(Integer permissionId, PermissionDto permissionDto) {
        Permission permission = permissionRepository.findByPermissionId(permissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Permission not found with id: " + permissionId));

        // If changing permission name, check if new name already exists
        if (!permission.getPermissionName().equals(permissionDto.getPermissionName()) &&
                permissionRepository.existsByPermissionName(permissionDto.getPermissionName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission name already exists");
        }

        permission.setPermissionName(permissionDto.getPermissionName());
        permission.setDescription(permissionDto.getDescription());

        permissionRepository.save(permission);

        return permissionMapper.mapPermissionToDto(permission);
    }

    // Delete role
    @Transactional
    public void deleteRole(Integer roleId) {
        Role role = roleRepository.findByRoleId(roleId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found with id: " + roleId));

        // Check if any users only have this one role
        List<User> usersWithOnlyThisRole = userRepository.findAll().stream()
                .filter(user -> user.getRoles().size() == 1 &&
                        user.getRoles().stream().anyMatch(r -> r.getRoleId().equals(roleId)))
                .collect(Collectors.toList());

        if (!usersWithOnlyThisRole.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot delete role. It is the only role assigned to " + usersWithOnlyThisRole.size()
                            + " user(s).");
        }

        // Remove the role from all users
        List<User> usersWithThisRole = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream().anyMatch(r -> r.getRoleId().equals(roleId)))
                .collect(Collectors.toList());

        for (User user : usersWithThisRole) {
            user.getRoles().removeIf(r -> r.getRoleId().equals(roleId));
        }

        userRepository.saveAll(usersWithThisRole); // Important: this updates the join table

        // Now delete the role safely
        roleRepository.delete(role);
    }

    // Delete permission
    @Transactional
    public void deletePermission(Integer permissionId) {
        if (!permissionRepository.existsByPermissionId(permissionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found with id: " + permissionId);
        }

        permissionRepository.deleteByPermissionId(permissionId);
    }

    // Helper methods to map entities to DTOs
    private UserRoleResponse mapUserToDto(User user) {
        UserRoleResponse dto = new UserRoleResponse();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(roleMapper::mapRoleToDto)
                .collect(Collectors.toSet()));
        return dto;
    }
}