package com.example.usermanagement.service;

import java.util.List;

import com.example.usermanagement.dto.PasswordUpdateDto;
import com.example.usermanagement.dto.StaffDto;
import com.example.usermanagement.dto.StaffResponseDto;
import com.example.usermanagement.dto.StaffUpdateRequestDto;
import com.example.usermanagement.model.Staff;

public interface StaffService {
    List<Staff> getAllStaff();

    Staff getStaffByUserId(Integer userId);

    StaffResponseDto getStaffResponseByUserId(Integer userId);

    Staff updateStaff(Long id, StaffDto staffDto);

    StaffResponseDto updateStaff(Integer userId, StaffUpdateRequestDto updateDto);

    boolean updatePassword(Integer userId, PasswordUpdateDto passwordUpdateDto);

    void deleteStaff(Integer userId);

    List<StaffResponseDto> getAllStaffResponses();
}
