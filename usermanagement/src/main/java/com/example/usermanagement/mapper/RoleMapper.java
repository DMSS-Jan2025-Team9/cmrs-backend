package com.example.usermanagement.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.usermanagement.dto.RoleDto;
import com.example.usermanagement.model.Role;

@Component
public class RoleMapper {

    private final PermissionMapper permissionMapper;

    public RoleMapper(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    public RoleDto mapRoleToDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setRoleId(role.getRoleId());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());
        dto.setPermissions(role.getPermissions().stream()
                .map(permissionMapper::mapPermissionToDto)
                .collect(Collectors.toSet()));
        return dto;
    }
}
