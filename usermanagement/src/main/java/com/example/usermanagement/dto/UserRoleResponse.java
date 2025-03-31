package com.example.usermanagement.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserRoleResponse {
    private Integer userId;
    private String username;
    private String email;
    private Set<RoleDto> roles;
}

