package com.example.usermanagement.mapper;

import org.springframework.stereotype.Component;

import com.example.usermanagement.dto.PermissionDto;
import com.example.usermanagement.model.Permission;

@Component
public class PermissionMapper {
    public PermissionDto mapPermissionToDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setPermissionId(permission.getPermissionId());
        dto.setPermissionName(permission.getPermissionName());
        dto.setDescription(permission.getDescription());
        return dto;
    }
}
