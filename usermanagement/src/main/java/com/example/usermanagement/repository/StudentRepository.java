package com.example.usermanagement.repository;

import com.example.usermanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByJobId(String jobId);  // Find students by jobId

    List<Student> findByProgramName(String programName);

    Optional <Student> findByUser_UserId(Integer userId);
    Optional <Student> deleteByUser_UserId(Integer userId);
}
