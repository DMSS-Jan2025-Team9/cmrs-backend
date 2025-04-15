package com.example.usermanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.usermanagement.dto.StaffDto;
import com.example.usermanagement.model.Staff;
import com.example.usermanagement.repository.StaffRepository;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Staff getStaffByUserId(Integer id) {
        return staffRepository.findByUser_UserId(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
    }

    public Staff updateStaff(Long id, StaffDto staffDto) {
    Staff staff = staffRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));

    staff.setFirstName(staffDto.getFirstName().trim());
    staff.setLastName(staffDto.getLastName().trim());
    staff.setName(staff.getFirstName() + " " + staff.getLastName());
    staff.setDepartment(staffDto.getDepartment());
    staff.setPosition(staffDto.getPosition());

    return staffRepository.save(staff);
}


    public void deleteStaff(Integer id) {
        staffRepository.deleteByUser_UserId(id);
    }
}
