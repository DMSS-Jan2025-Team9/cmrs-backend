package com.example.coursemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.coursemanagement.model.Program;

public interface ProgramRepository extends JpaRepository<Program, Integer> {
    // JpaRepository already has built-in methods for basic CRUD operations
}
