package com.example.coursemanagement.repository;

import com.example.coursemanagement.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    // JpaRepository already has built-in methods for basic CRUD operations
}
