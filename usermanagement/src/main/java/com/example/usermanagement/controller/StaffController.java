package com.example.usermanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.usermanagement.dto.StaffDto;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.service.StaffService;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    
    @Autowired
    private StaffService staffService;

    @GetMapping("/all")
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Integer id) {
        return ResponseEntity.ok(staffService.getStaffByUserId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(
            @PathVariable Long id,
            @RequestBody StaffDto staffDto
    ) {
        Staff updatedStaff = staffService.updateStaff(id, staffDto);
        return ResponseEntity.ok(updatedStaff);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Integer id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}
