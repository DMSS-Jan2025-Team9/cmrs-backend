package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.model.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(role -> role.getRoleName())
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
