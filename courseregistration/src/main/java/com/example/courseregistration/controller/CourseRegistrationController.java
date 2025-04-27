package com.example.courseregistration.controller;

import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.CreateRegistrationDTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
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
            @RequestParam(required = false) Long groupRegistrationId,
            @RequestParam(required = false) Boolean groupRegistration) {
        return courseRegistrationService.filterRegistration(registrationId, studentId, classId, registrationStatus, groupRegistrationId, groupRegistration);
    }
    
    @PostMapping
    public ResponseEntity<List<RegistrationDTO>> createRegistration(@RequestBody CreateRegistrationDTO createRegistrationDTO) {
        List<RegistrationDTO> createdRegistrations = courseRegistrationService.createRegistration(createRegistrationDTO);
        return new ResponseEntity<>(createdRegistrations, HttpStatus.CREATED);
    }

    @PutMapping("/status")
    public ResponseEntity<List<RegistrationDTO>> updateRegistrationStatus(
            @RequestBody UpdateRegistrationStatusDTO dto) {
        List<RegistrationDTO> updated = courseRegistrationService.updateRegistrationStatus(dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/unenroll/{registrationId}")
    public ResponseEntity<RegistrationDTO> unenrollRegistration(@PathVariable Long registrationId) {
        RegistrationDTO unenrolledRegistration = courseRegistrationService.unenrollRegistration(registrationId);
        return ResponseEntity.ok(unenrolledRegistration);
    }


}

    


