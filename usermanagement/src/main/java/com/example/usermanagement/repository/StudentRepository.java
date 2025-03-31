package com.example.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.usermanagement.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
