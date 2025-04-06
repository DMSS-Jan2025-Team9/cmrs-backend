package com.example.usermanagement.dto;

import lombok.Data;

@Data
public class PermissionDto {
    private Integer permissionId;
    private String permissionName;
    private String description;
}
