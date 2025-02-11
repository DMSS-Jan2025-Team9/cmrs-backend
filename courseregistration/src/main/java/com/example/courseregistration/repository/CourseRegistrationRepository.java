package com.example.courseregistration.repository;

import com.example.courseregistration.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRepository extends JpaRepository<Registration, Long> {

    // Find all registrations by student ID
    List<Registration> findByStudentId(Long studentId);

    // Find all registrations by group registration ID
    List<Registration> findByGroupRegistrationId(Long groupRegistrationId);

    // Find a registration by registration ID
    Optional<Registration> findById(Long registrationId);
}
