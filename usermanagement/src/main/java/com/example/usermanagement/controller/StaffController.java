package com.example.usermanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.usermanagement.dto.ApiResponse;
import com.example.usermanagement.dto.PasswordUpdateDto;
import com.example.usermanagement.dto.StaffDto;
import com.example.usermanagement.dto.StaffResponseDto;
import com.example.usermanagement.dto.StaffUpdateRequestDto;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.service.StaffService;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping("/all")
    // @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<List<StaffResponseDto>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaffResponses());
    }

    @GetMapping("/{userId}")
    // @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_staff')")
    public ResponseEntity<StaffResponseDto> getStaffById(@PathVariable Integer userId) {
        return ResponseEntity.ok(staffService.getStaffResponseByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(
            @PathVariable Long id,
            @RequestBody StaffDto staffDto) {
        Staff updatedStaff = staffService.updateStaff(id, staffDto);
        return ResponseEntity.ok(updatedStaff);
    }

    @PutMapping("/update/{userId}")
    // @PreAuthorize("hasAnyRole('ROLE_admin')")
    public ResponseEntity<StaffResponseDto> updateStaffWithRoles(
            @PathVariable Integer userId,
            @RequestBody StaffUpdateRequestDto updateDto) {
        StaffResponseDto updatedStaff = staffService.updateStaff(userId, updateDto);
        return ResponseEntity.ok(updatedStaff);
    }

    @PostMapping("/{userId}/password")
    // @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_staff')")
    public ResponseEntity<ApiResponse> updatePassword(
            @PathVariable Integer userId,
            @RequestBody PasswordUpdateDto passwordUpdateDto) {
        boolean updated = staffService.updatePassword(userId, passwordUpdateDto);

        if (updated) {
            return ResponseEntity.ok(new ApiResponse(true, "Password updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Failed to update password. Please check your current password."));
        }
    }

    @DeleteMapping("/{userId}")
    // @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<ApiResponse> deleteStaff(@PathVariable Integer userId) {
        staffService.deleteStaff(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Staff deleted successfully"));
    }
}
