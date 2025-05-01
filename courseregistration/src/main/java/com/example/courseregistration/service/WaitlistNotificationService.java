package com.example.courseregistration.service;

import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.StudentDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for handling waitlist notifications
 * This centralizes the logic for notifying waitlisted students when vacancies
 * become available
 */
@Service
public class WaitlistNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(WaitlistNotificationService.class);

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final MicroserviceClient microserviceClient;
    private final NotificationPublisherService notificationPublisherService;

    public WaitlistNotificationService(
            CourseRegistrationRepository courseRegistrationRepository,
            MicroserviceClient microserviceClient,
            NotificationPublisherService notificationPublisherService) {
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.microserviceClient = microserviceClient;
        this.notificationPublisherService = notificationPublisherService;
    }

    /**
     * Notifies all waitlisted students for a specific class when a vacancy becomes
     * available
     * 
     * @param classId     The ID of the class that has a new vacancy
     * @param courseClass The class details including course information
     */
    public void notifyWaitlistedStudents(Long classId, CourseClassDTO courseClass) {
        logger.debug("Checking for waitlisted students for class: {}", classId);

        // Find students who are waitlisted for this class
        List<Registration> waitlistedRegs = courseRegistrationRepository.filterRegistration(
                null, null, classId, "Waitlisted", null);

        logger.info("Found {} waitlisted students for class: {}", waitlistedRegs.size(), classId);

        // Notify each waitlisted student about the vacancy
        for (Registration waitlistedReg : waitlistedRegs) {
            try {
                notifyStudent(waitlistedReg.getStudentId(), courseClass);
            } catch (Exception e) {
                logger.error("Error sending notification to student {}: {}",
                        waitlistedReg.getStudentId(), e.getMessage(), e);

                // Continue with the next student even if this one fails
            }
        }
    }

    /**
     * Notifies a specific student about an available vacancy
     * 
     * @param studentId   The numeric ID of the student to notify
     * @param courseClass The class details including course information
     */
    public void notifyStudent(Long studentId, CourseClassDTO courseClass) {
        logger.debug("Sending vacancy notification to student with ID: {}", studentId);

        try {
            // Get the student details to fetch the studentFullId
            StudentDTO student = microserviceClient.fetchStudentById(studentId);

            if (student != null && student.getStudentFullId() != null) {
                // Use the proper student full ID for notification
                logger.debug("Sending vacancy notification to student with fullId: {}", student.getStudentFullId());
                notificationPublisherService.publishVacancyAvailableNotification(
                        student.getStudentFullId(), studentId, courseClass);
            } else {
                // Fallback to the legacy method if studentFullId is not available
                logger.debug("Student fullId not available, using numeric ID: {}", studentId);
                notificationPublisherService.publishVacancyAvailableNotification(
                        studentId, courseClass);
            }
        } catch (Exception e) {
            logger.error("Error sending notification to student {}: {}", studentId, e.getMessage(), e);
            throw e; // Re-throw to allow caller to handle the error
        }
    }
}