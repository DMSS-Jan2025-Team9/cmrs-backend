package com.example.courseregistration.controller;

import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courseRegistration")
public class CourseRegistrationController {

    @Autowired
    private CourseRegistrationRepository courseRegistrationRepository;

    // Get all registrations
    @GetMapping
    public List<Registration> getAllRegistrations() {
        return courseRegistrationRepository.findAll();
    }

    // Get all registrations by student ID
    @GetMapping("/student/{studentId}")
    public List<Registration> getRegistrationsByStudentId(@PathVariable Long studentId) {
        return courseRegistrationRepository.findByStudentId(studentId);
    }

    // Get all registrations by group registration ID
    @GetMapping("/group/{groupRegistrationId}")
    public List<Registration> getRegistrationsByGroupRegistrationId(@PathVariable Long groupRegistrationId) {
        return courseRegistrationRepository.findByGroupRegistrationId(groupRegistrationId);
    }

    // Get registration by registration ID
    @GetMapping("/{registrationId}")
    public Optional<Registration> getRegistrationById(@PathVariable Long registrationId) {
        return courseRegistrationRepository.findById(registrationId);
    }

    // Create a new registration
    @PostMapping
    public Registration createRegistration(@RequestBody Registration registration) {
        return courseRegistrationRepository.save(registration);
    }

    // Update an existing registration
    @PutMapping("/{registrationId}")
    public Registration updateRegistration(@PathVariable Long registrationId, @RequestBody Registration registration) {
        if (courseRegistrationRepository.existsById(registrationId)) {
            registration.setRegistrationId(registrationId);
            return courseRegistrationRepository.save(registration);
        } else {
            return null;  // Registration not found
        }
    }

    // Delete a registration by ID
    @DeleteMapping("/{registrationId}")
    public void deleteRegistration(@PathVariable Long registrationId) {
        courseRegistrationRepository.deleteById(registrationId);
    }
}
    


