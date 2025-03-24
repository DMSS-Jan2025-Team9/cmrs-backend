package com.example.usermanagement.repository;

import com.example.usermanagement.dto.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByJobId(String jobId);  // Find students by jobId
}
