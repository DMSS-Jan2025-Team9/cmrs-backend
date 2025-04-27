package com.example.courseregistration.controller;

import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.CreateRegistrationDTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.service.CourseRegistrationService;
import com.example.courseregistration.service.NotificationPublisherService;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courseRegistration")
public class CourseRegistrationController {

    private final CourseRegistrationService courseRegistrationService;
    private final NotificationPublisherService notificationPublisherService;
    private final MicroserviceClient microserviceClient;

    public CourseRegistrationController(
            CourseRegistrationService courseRegistrationService,
            NotificationPublisherService notificationPublisherService,
            MicroserviceClient microserviceClient) {
        this.courseRegistrationService = courseRegistrationService;
        this.notificationPublisherService = notificationPublisherService;
        this.microserviceClient = microserviceClient;
    }

    @GetMapping
    public List<RegistrationDTO> getAllRegistrations(
            @RequestParam(required = false) Long registrationId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String registrationStatus,
            @RequestParam(required = false) Long groupRegistrationId,
            @RequestParam(required = false) Boolean groupRegistration) {
        return courseRegistrationService.filterRegistration(registrationId, studentId, classId, registrationStatus,
                groupRegistrationId, groupRegistration);
    }

    @PostMapping
    public ResponseEntity<List<RegistrationDTO>> createRegistration(
            @RequestBody CreateRegistrationDTO createRegistrationDTO) {
        List<RegistrationDTO> createdRegistrations = courseRegistrationService
                .createRegistration(createRegistrationDTO);
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

    // Testing endpoints for waitlist functionality

    /**
     * Endpoint to test the waitlist notification
     * 
     * @param studentId The ID of the student to notify
     * @param classId   The ID of the class for which the student is waitlisted
     * @return ResponseEntity with status
     */
    @PostMapping("/waitlist-notification")
    public ResponseEntity<Map<String, String>> testWaitlistNotification(
            @RequestParam Long studentId,
            @RequestParam Long classId) {

        CourseClassDTO courseClass = microserviceClient.fetchClass(classId);
        notificationPublisherService.publishWaitlistNotification(studentId, courseClass);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Waitlist notification sent for student " + studentId + " and class " + classId));
    }

    /**
     * Endpoint to test the vacancy available notification
     * 
     * @param studentId The ID of the student to notify
     * @param classId   The ID of the class that has vacancy
     * @return ResponseEntity with status
     */
    @PostMapping("/vacancy-notification")
    public ResponseEntity<Map<String, String>> testVacancyNotification(
            @RequestParam Long studentId,
            @RequestParam Long classId) {

        CourseClassDTO courseClass = microserviceClient.fetchClass(classId);
        notificationPublisherService.publishVacancyAvailableNotification(studentId, courseClass);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Vacancy notification sent for student " + studentId + " and class " + classId));
    }

    /**
     * Endpoint to simulate updating the vacancy of a class
     * 
     * @param classId The ID of the class to update
     * @param vacancy The new vacancy count
     * @return ResponseEntity with the updated class information
     */
    @PutMapping("/update-vacancy")
    public ResponseEntity<CourseClassDTO> testUpdateVacancy(
            @RequestParam Long classId,
            @RequestParam int vacancy) {

        CourseClassDTO courseClass = microserviceClient.fetchClass(classId);
        microserviceClient.updateVacancy(courseClass, vacancy);

        // Fetch the updated class data
        CourseClassDTO updatedClass = microserviceClient.fetchClass(classId);
        return ResponseEntity.ok(updatedClass);
    }
}
