package com.example.usermanagement.service.impl;

import com.example.usermanagement.dto.PasswordUpdateDto;
import com.example.usermanagement.dto.StaffDto;
import com.example.usermanagement.dto.StaffResponseDto;
import com.example.usermanagement.dto.StaffUpdateRequestDto;
import com.example.usermanagement.mapper.StaffMapper;
import com.example.usermanagement.model.Role;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.StaffRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StaffServiceImpl(StaffRepository staffRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @Override
    public Staff getStaffByUserId(Integer userId) {
        return staffRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Staff not found with user id: " + userId));
    }

    @Override
    public StaffResponseDto getStaffResponseByUserId(Integer userId) {
        Staff staff = getStaffByUserId(userId);
        return StaffMapper.toResponseDto(staff);
    }

    @Override
    public Staff updateStaff(Long id, StaffDto staffDto) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));

        staff.setFirstName(staffDto.getFirstName().trim());
        staff.setLastName(staffDto.getLastName().trim());
        staff.setName(staff.getFirstName() + " " + staff.getLastName());
        staff.setDepartment(staffDto.getDepartment());
        staff.setPosition(staffDto.getPosition());
        staff.setStaffFullId(staffDto.getStaffFullId());

        return staffRepository.save(staff);
    }

    @Override
    @Transactional
    public StaffResponseDto updateStaff(Integer userId, StaffUpdateRequestDto updateDto) {
        Staff staff = getStaffByUserId(userId);

        // Update the staff entity from the DTO
        StaffMapper.updateStaffFromDto(staff, updateDto);

        // Update roles if provided
        if (updateDto.getRoles() != null && !updateDto.getRoles().isEmpty() && staff.getUser() != null) {
            User user = staff.getUser();
            Set<Role> newRoles = updateDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByRoleName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(newRoles);
            userRepository.save(user);
        }

        // Save the updated staff
        staff = staffRepository.save(staff);

        // If email was updated in the user entity
        if (updateDto.getEmail() != null && staff.getUser() != null) {
            userRepository.save(staff.getUser());
        }

        return StaffMapper.toResponseDto(staff);
    }

    @Override
    @Transactional
    public boolean updatePassword(Integer userId, PasswordUpdateDto passwordUpdateDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Verify current password matches
        if (!passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(), user.getPassword())) {
            return false;
        }

        // Verify new password and confirm password match
        if (!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmPassword())) {
            return false;
        }

        // Encode and update the password
        user.setPassword(passwordEncoder.encode(passwordUpdateDto.getNewPassword()));
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public void deleteStaff(Integer userId) {
        staffRepository.deleteByUser_UserId(userId);
    }

    @Override
    public List<StaffResponseDto> getAllStaffResponses() {
        return staffRepository.findAll().stream()
                .map(StaffMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}