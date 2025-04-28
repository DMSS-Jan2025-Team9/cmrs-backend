package com.example.usermanagement.dto;

import java.util.Set;

import lombok.Data;

@Data
public class RolePermissionRequest {
    private Integer roleId;
    private Set<Integer> permissionIds;
}
