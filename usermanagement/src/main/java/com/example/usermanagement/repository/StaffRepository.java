package com.example.usermanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.usermanagement.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional <Staff> findByUser_UserId(Integer userId);
    Optional <Staff> deleteByUser_UserId(Integer userId);
}
