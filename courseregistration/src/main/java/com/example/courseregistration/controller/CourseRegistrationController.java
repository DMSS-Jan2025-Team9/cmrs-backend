package com.example.courseregistration.controller;

import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.service.CourseRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courseRegistration")
public class CourseRegistrationController {

    private final CourseRegistrationService courseRegistrationService;

    public CourseRegistrationController(CourseRegistrationService courseRegistrationService) {
        this.courseRegistrationService = courseRegistrationService;
    }

    @GetMapping
    public List<RegistrationDTO> getAllRegistrations(
            @RequestParam(required = false) Long registrationId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String registrationStatus,
            @RequestParam(required = false) Long groupRegistrationId) {
        return courseRegistrationService.filterRegistration(registrationId, studentId, classId, registrationStatus, groupRegistrationId);
    }
    
    @PostMapping
    public ResponseEntity<RegistrationDTO> createRegistration(@RequestBody RegistrationDTO registrationDTO) {
        RegistrationDTO createdRegistration = courseRegistrationService.createRegistration(registrationDTO);
        return new ResponseEntity<>(createdRegistration, HttpStatus.CREATED);
    }
}

    


