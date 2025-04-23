package com.example.usermanagement.dto;

import java.util.Set;

import lombok.Data;

@Data
public class RoleDto {
    private Integer roleId;
    private String roleName;
    private String description;
    private Set<PermissionDto> permissions;
}
