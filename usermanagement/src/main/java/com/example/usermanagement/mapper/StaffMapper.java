package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.StaffResponseDto;
import com.example.usermanagement.dto.StaffUpdateRequestDto;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class StaffMapper {

    public static StaffResponseDto toResponseDto(Staff staff) {
        if (staff == null) {
            return null;
        }

        User user = staff.getUser();
        List<String> roles = null;

        if (user != null && user.getRoles() != null) {
            roles = user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
        }

        return StaffResponseDto.builder()
                .staffId(staff.getStaffId())
                .userId(user != null ? user.getUserId() : null)
                .username(user != null ? user.getUsername() : null)
                .email(user != null ? user.getEmail() : null)
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .fullName(staff.getName())
                .staffFullId(staff.getStaffFullId())
                .department(staff.getDepartment())
                .position(staff.getPosition())
                .roles(roles)
                .build();
    }

    public static void updateStaffFromDto(Staff staff, StaffUpdateRequestDto dto) {
        if (staff == null || dto == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            staff.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            staff.setLastName(dto.getLastName());
        }

        if (dto.getFirstName() != null && dto.getLastName() != null) {
            staff.setName(dto.getFirstName() + " " + dto.getLastName());
        }

        if (dto.getStaffFullId() != null) {
            staff.setStaffFullId(dto.getStaffFullId());
        }

        if (dto.getDepartment() != null) {
            staff.setDepartment(dto.getDepartment());
        }

        if (dto.getPosition() != null) {
            staff.setPosition(dto.getPosition());
        }

        // Update user email if provided
        if (dto.getEmail() != null && staff.getUser() != null) {
            staff.getUser().setEmail(dto.getEmail());
        }

        // Note: Roles will be handled separately in the service layer
    }
}