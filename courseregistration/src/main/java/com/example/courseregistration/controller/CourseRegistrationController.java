package com.example.courseregistration.controller;

import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.CreateRegistrationDTO;
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
    public ResponseEntity<List<RegistrationDTO>> createRegistration(@RequestBody CreateRegistrationDTO createRegistrationDTO) {
        List<RegistrationDTO> createdRegistrations = courseRegistrationService.createRegistration(createRegistrationDTO);
        return new ResponseEntity<>(createdRegistrations, HttpStatus.CREATED);
    }

    @PutMapping("/individual/{registrationId}")
    public ResponseEntity<RegistrationDTO> updateIndividualRegistrationStatus(
            @PathVariable Long registrationId,
            @RequestParam String newStatus) {
        RegistrationDTO updatedRegistration = courseRegistrationService.updateIndividualRegistrationStatus(registrationId, newStatus);
        return ResponseEntity.ok(updatedRegistration);
    }
    
    @PutMapping("/group/{groupRegistrationId}")
    public ResponseEntity<List<RegistrationDTO>> updateGroupRegistrationStatus(
            @PathVariable Long groupRegistrationId,
            @RequestParam String newStatus) {
        List<RegistrationDTO> updatedRegistrations = courseRegistrationService.updateGroupRegistrationStatus(groupRegistrationId, newStatus);
        return ResponseEntity.ok(updatedRegistrations);
    }


}

    


