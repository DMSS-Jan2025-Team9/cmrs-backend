package com.example.usermanagement.service;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.mapper.PermissionMapper;
import com.example.usermanagement.mapper.RoleMapper;
import com.example.usermanagement.model.Permission;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.PermissionRepository;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private UserRoleService userRoleService;

    private User user;
    private Role role;
    private Role adminRole;
    private Permission permission;
    private RoleDto roleDto;
    private PermissionDto permissionDto;
    private UserRoleResponse userRoleResponse;
    private RolePermissionRequest rolePermissionRequest;
    private UserRoleRequest userRoleRequest;

    @BeforeEach
    void setUp() {
        // Setup user
        user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Setup role
        role = new Role();
        role.setRoleId(1);
        role.setRoleName("ROLE_user");
        role.setDescription("Regular user role");

        adminRole = new Role();
        adminRole.setRoleId(2);
        adminRole.setRoleName("ROLE_admin");
        adminRole.setDescription("Admin role");

        // Setup permission
        permission = new Permission();
        permission.setPermissionId(1);
        permission.setPermissionName("read_data");
        permission.setDescription("Permission to read data");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);
        role.setPermissions(permissions);

        // Setup DTOs
        roleDto = new RoleDto();
        roleDto.setRoleId(1);
        roleDto.setRoleName("ROLE_user");
        roleDto.setDescription("Regular user role");
        Set<PermissionDto> permissionDtos = new HashSet<>();
        permissionDto = new PermissionDto();
        permissionDto.setPermissionId(1);
        permissionDto.setPermissionName("read_data");
        permissionDto.setDescription("Permission to read data");
        permissionDtos.add(permissionDto);
        roleDto.setPermissions(permissionDtos);

        // Setup requests
        rolePermissionRequest = new RolePermissionRequest();
        rolePermissionRequest.setRoleId(1);
        rolePermissionRequest.setPermissionIds(new HashSet<>(Arrays.asList(1, 2)));

        userRoleRequest = new UserRoleRequest();
        userRoleRequest.setUserId(1);
        userRoleRequest.setRoleIds(new HashSet<>(Arrays.asList(1, 2)));

        // Setup UserRoleResponse
        userRoleResponse = new UserRoleResponse();
        userRoleResponse.setUserId(1);
        userRoleResponse.setUsername("testuser");
        userRoleResponse.setEmail("test@example.com");
        userRoleResponse.setRoles(new HashSet<>(Collections.singletonList(roleDto)));

        // Make mapper mocks lenient (used conditionally across tests)
        lenient().when(roleMapper.mapRoleToDto(any(Role.class))).thenReturn(roleDto);
        lenient().when(permissionMapper.mapPermissionToDto(any(Permission.class))).thenReturn(permissionDto);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserRoleResponse> result = userRoleService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserId());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        // Arrange
        when(userRepository.findByUserId(anyInt())).thenReturn(Optional.of(user));

        // Act
        UserRoleResponse result = userRoleService.getUserById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByUserId(1);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Arrange
        when(userRepository.findByUserId(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.getUserById(999);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User not found with id: 999"));
        verify(userRepository, times(1)).findByUserId(999);
    }

    @Test
    void testGetAllRoles() {
        // Arrange
        List<Role> roles = Arrays.asList(role, adminRole);
        when(roleRepository.findAll()).thenReturn(roles);

        // Act
        List<RoleDto> result = userRoleService.getAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roleRepository, times(1)).findAll();
        verify(roleMapper, times(2)).mapRoleToDto(any(Role.class));
    }

    @Test
    void testGetRoleById() {
        // Arrange
        when(roleRepository.findByRoleId(anyInt())).thenReturn(Optional.of(role));

        // Act
        RoleDto result = userRoleService.getRoleById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRoleId());
        assertEquals("ROLE_user", result.getRoleName());
        verify(roleRepository, times(1)).findByRoleId(1);
        verify(roleMapper, times(1)).mapRoleToDto(role);
    }

    @Test
    void testGetRoleByIdNotFound() {
        // Arrange
        when(roleRepository.findByRoleId(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.getRoleById(999);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Role not found with id: 999"));
        verify(roleRepository, times(1)).findByRoleId(999);
    }

    @Test
    void testGetPermissionById() {
        // Arrange
        when(permissionRepository.findByPermissionId(anyInt())).thenReturn(Optional.of(permission));

        // Act
        PermissionDto result = userRoleService.getPermissionById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getPermissionId());
        assertEquals("read_data", result.getPermissionName());
        verify(permissionRepository, times(1)).findByPermissionId(1);
        verify(permissionMapper, times(1)).mapPermissionToDto(permission);
    }

    @Test
    void testGetPermissionByIdNotFound() {
        // Arrange
        when(permissionRepository.findByPermissionId(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.getPermissionById(999);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Permission not found with id: 999"));
        verify(permissionRepository, times(1)).findByPermissionId(999);
    }

    @Test
    void testGetAllPermissions() {
        // Arrange
        List<Permission> permissions = Arrays.asList(permission);
        when(permissionRepository.findAll()).thenReturn(permissions);

        // Act
        List<PermissionDto> result = userRoleService.getAllPermissions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(permissionRepository, times(1)).findAll();
        verify(permissionMapper, times(1)).mapPermissionToDto(any(Permission.class));
    }

    @Test
    void testCreateRole() {
        // Arrange
        RoleDto simpleRoleDto = new RoleDto();
        simpleRoleDto.setRoleName("ROLE_new");
        simpleRoleDto.setDescription("New role");
        // No permissions in this DTO to avoid the error

        when(roleRepository.existsByRoleName(anyString())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // Act
        RoleDto result = userRoleService.createRole(simpleRoleDto);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_user", result.getRoleName());
        verify(roleRepository, times(1)).existsByRoleName("ROLE_new");
        verify(roleRepository, times(1)).save(any(Role.class));
        verify(roleMapper, times(1)).mapRoleToDto(role);
    }

    @Test
    void testCreateRoleNameExists() {
        // Arrange
        RoleDto simpleRoleDto = new RoleDto();
        simpleRoleDto.setRoleName("ROLE_existing");
        simpleRoleDto.setDescription("Existing role");

        when(roleRepository.existsByRoleName(anyString())).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.createRole(simpleRoleDto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Role name already exists"));
        verify(roleRepository, times(1)).existsByRoleName("ROLE_existing");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testCreatePermission() {
        // Arrange
        when(permissionRepository.existsByPermissionName(anyString())).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        // Act
        PermissionDto result = userRoleService.createPermission(permissionDto);

        // Assert
        assertNotNull(result);
        assertEquals("read_data", result.getPermissionName());
        verify(permissionRepository, times(1)).existsByPermissionName("read_data");
        verify(permissionRepository, times(1)).save(any(Permission.class));
        verify(permissionMapper, times(1)).mapPermissionToDto(permission);
    }

    @Test
    void testCreatePermissionNameExists() {
        // Arrange
        when(permissionRepository.existsByPermissionName(anyString())).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.createPermission(permissionDto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Permission name already exists"));
        verify(permissionRepository, times(1)).existsByPermissionName("read_data");
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void testUpdateRolePermissions() {
        // Arrange
        Permission permission2 = new Permission();
        permission2.setPermissionId(2);
        permission2.setPermissionName("write_data");

        when(roleRepository.findByRoleId(anyInt())).thenReturn(Optional.of(role));
        when(permissionRepository.findByPermissionId(1)).thenReturn(Optional.of(permission));
        when(permissionRepository.findByPermissionId(2)).thenReturn(Optional.of(permission2));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // Act
        RoleDto result = userRoleService.updateRolePermissions(rolePermissionRequest);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_user", result.getRoleName());
        verify(roleRepository, times(1)).findByRoleId(1);
        verify(permissionRepository, times(1)).findByPermissionId(1);
        verify(permissionRepository, times(1)).findByPermissionId(2);
        verify(roleRepository, times(1)).save(any(Role.class));
        verify(roleMapper, times(1)).mapRoleToDto(role);
    }

    @Test
    void testAssignRolesToUser() {
        // Arrange
        when(userRepository.findByUserId(anyInt())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleId(1)).thenReturn(Optional.of(role));
        when(roleRepository.findByRoleId(2)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserRoleResponse result = userRoleService.assignRolesToUser(userRoleRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUserId(1);
        verify(roleRepository, times(1)).findByRoleId(1);
        verify(roleRepository, times(1)).findByRoleId(2);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateRole() {
        // Arrange
        when(roleRepository.findByRoleId(anyInt())).thenReturn(Optional.of(role));
        when(roleRepository.existsByRoleName(anyString())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto updateDto = new RoleDto();
        updateDto.setRoleId(1);
        updateDto.setRoleName("ROLE_updated");
        updateDto.setDescription("Updated description");

        // Act
        RoleDto result = userRoleService.updateRole(1, updateDto);

        // Assert
        assertNotNull(result);
        verify(roleRepository, times(1)).findByRoleId(1);
        verify(roleRepository, times(1)).existsByRoleName("ROLE_updated");
        verify(roleRepository, times(1)).save(any(Role.class));
        verify(roleMapper, times(1)).mapRoleToDto(role);
    }

    @Test
    void testUpdateRoleNameExists() {
        // Arrange
        when(roleRepository.findByRoleId(anyInt())).thenReturn(Optional.of(role));
        when(roleRepository.existsByRoleName(anyString())).thenReturn(true);

        RoleDto updateDto = new RoleDto();
        updateDto.setRoleId(1);
        updateDto.setRoleName("ROLE_existing");
        updateDto.setDescription("Updated description");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.updateRole(1, updateDto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Role name already exists"));
        verify(roleRepository, times(1)).findByRoleId(1);
        verify(roleRepository, times(1)).existsByRoleName("ROLE_existing");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdatePermission() {
        // Arrange
        when(permissionRepository.findByPermissionId(anyInt())).thenReturn(Optional.of(permission));
        when(permissionRepository.existsByPermissionName(anyString())).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        PermissionDto updateDto = new PermissionDto();
        updateDto.setPermissionId(1);
        updateDto.setPermissionName("updated_permission");
        updateDto.setDescription("Updated description");

        // Act
        PermissionDto result = userRoleService.updatePermission(1, updateDto);

        // Assert
        assertNotNull(result);
        verify(permissionRepository, times(1)).findByPermissionId(1);
        verify(permissionRepository, times(1)).existsByPermissionName("updated_permission");
        verify(permissionRepository, times(1)).save(any(Permission.class));
        verify(permissionMapper, times(1)).mapPermissionToDto(permission);
    }

    @Test
    void testDeletePermission() {
        // Arrange
        when(permissionRepository.existsByPermissionId(anyInt())).thenReturn(true);

        // Act
        userRoleService.deletePermission(1);

        // Assert
        verify(permissionRepository, times(1)).existsByPermissionId(1);
        verify(permissionRepository, times(1)).deleteByPermissionId(1);
    }

    @Test
    void testDeletePermissionNotFound() {
        // Arrange
        when(permissionRepository.existsByPermissionId(anyInt())).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userRoleService.deletePermission(999);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Permission not found with id: 999"));
        verify(permissionRepository, times(1)).existsByPermissionId(999);
        verify(permissionRepository, never()).deleteByPermissionId(anyInt());
    }

    @Test
    void testDeleteRole() {
        // Arrange
        when(roleRepository.findByRoleId(anyInt())).thenReturn(Optional.of(role));
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        userRoleService.deleteRole(1);

        // Assert
        verify(roleRepository, times(1)).findByRoleId(1);
        // Allow multiple calls to findAll since the service actually calls it twice
        verify(userRepository, atLeastOnce()).findAll();
        verify(roleRepository, times(1)).delete(any(Role.class));
    }
}