package com.example.usermanagement.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserRoleRequest {
    private Integer userId;
    private Set<Integer> roleIds;
}
