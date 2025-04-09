package com.example.usermanagement.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {
    private Long userId;
    private String username;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private List<String> roles; // role names only
}
