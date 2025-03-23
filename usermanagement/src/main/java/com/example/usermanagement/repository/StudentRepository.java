package com.example.usermanagement.repository;

import com.example.usermanagement.dto.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
