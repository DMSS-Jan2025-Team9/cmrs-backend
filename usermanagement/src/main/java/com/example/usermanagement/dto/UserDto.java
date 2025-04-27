package com.example.usermanagement.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private Integer userId;
    private String username;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private List<String> roles;
}
