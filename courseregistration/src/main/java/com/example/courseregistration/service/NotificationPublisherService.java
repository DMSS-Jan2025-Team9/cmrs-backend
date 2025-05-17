package com.example.courseregistration.service;

import com.example.courseregistration.config.RabbitMQConfig;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.NotificationEventDTO;
import com.example.courseregistration.dto.StudentDTO;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Service
public class NotificationPublisherService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationPublisherService.class);

    private final RabbitTemplate rabbitTemplate;
    private final MicroserviceClient microserviceClient;

    public NotificationPublisherService(RabbitTemplate rabbitTemplate, MicroserviceClient microserviceClient) {
        this.rabbitTemplate = rabbitTemplate;
        this.microserviceClient = microserviceClient;
    }

    /**
     * Publishes a waitlist notification for a student
     * 
     * @param studentId     The student's numeric ID (for backward compatibility)
     * @param studentFullId The student's full ID string
     * @param courseClass   The course class details
     */
    public void publishWaitlistNotification(String studentFullId, Long studentId, CourseClassDTO courseClass) {
        // Verify course details are available
        logger.debug("Publishing waitlist notification for student {} and class {}", studentFullId,
                courseClass.getClassId());

        // Ensure we have complete course details
        ensureCourseDetailsAreComplete(courseClass);

        // Set proper course code and name
        String courseCode = courseClass.getCourseCode();
        String courseName = courseClass.getCourseName();

        logger.debug("Using course details: code={}, name={}", courseCode, courseName);

        String message = "You have been waitlisted for " + courseCode + " - " + courseName;
        logger.debug("Creating notification with message: {}", message);

        NotificationEventDTO event = new NotificationEventDTO(
                studentFullId,
                studentId,
                courseClass.getClassId(),
                courseCode,
                courseName,
                message,
                "WAITLISTED");

        logger.debug("Sending notification event to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.WAITLIST_ROUTING_KEY,
                event);
        logger.info("Waitlist notification published for student {} and course {}", studentFullId, courseCode);
    }

    /**
     * Overloaded method for backward compatibility (not in use)
     */
    public void publishWaitlistNotification(Long studentId, CourseClassDTO courseClass) {
        // For existing method calls, try to get the studentFullId if possible
        String studentFullId = null;
        try {
            StudentDTO student = microserviceClient.fetchStudentById(studentId);
            if (student != null && student.getStudentFullId() != null) {
                studentFullId = student.getStudentFullId();
            }
        } catch (Exception e) {
            logger.warn("Couldn't fetch student full ID for studentId: {}. Will use numeric ID only.", studentId);
        }

        publishWaitlistNotification(studentFullId, studentId, courseClass);
    }

    /**
     * Publishes a vacancy available notification for a student
     * 
     * @param studentFullId The student's full ID string
     * @param studentId     The student's numeric ID (for backward compatibility)
     * @param courseClass   The course class details
     */
    public void publishVacancyAvailableNotification(String studentFullId, Long studentId, CourseClassDTO courseClass) {
        // Verify course details are available
        logger.debug("Publishing vacancy notification for student {} and class {}", studentFullId,
                courseClass.getClassId());

        // Ensure we have complete course details
        ensureCourseDetailsAreComplete(courseClass);

        // Set proper course code and name
        String courseCode = courseClass.getCourseCode();
        String courseName = courseClass.getCourseName();

        logger.debug("Using course details: code={}, name={}", courseCode, courseName);

        String message = "A vacancy is now available for " + courseCode + " - " + courseName + ". Please register now!";
        logger.debug("Creating notification with message: {}", message);

        NotificationEventDTO event = new NotificationEventDTO(
                studentFullId,
                studentId,
                courseClass.getClassId(),
                courseCode,
                courseName,
                message,
                "VACANCY_AVAILABLE");

        logger.debug("Sending notification event to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.VACANCY_ROUTING_KEY,
                event);
        logger.info("Vacancy notification published for student {} and course {}", studentFullId, courseCode);
    }

    /**
     * Overloaded method for backward compatibility (not in use)
     */
    public void publishVacancyAvailableNotification(Long studentId, CourseClassDTO courseClass) {
        // For existing method calls, try to get the studentFullId if possible
        String studentFullId = null;
        try {
            StudentDTO student = microserviceClient.fetchStudentById(studentId);
            if (student != null && student.getStudentFullId() != null) {
                studentFullId = student.getStudentFullId();
            }
        } catch (Exception e) {
            logger.warn("Couldn't fetch student full ID for studentId: {}. Will use numeric ID only.", studentId);
        }

        publishVacancyAvailableNotification(studentFullId, studentId, courseClass);
    }

    /**
     * Helper method to ensure course details are complete
     * Attempts to fetch course details if missing
     */
    private void ensureCourseDetailsAreComplete(CourseClassDTO courseClass) {
        if (courseClass.getCourseCode() == null || courseClass.getCourseName() == null ||
                courseClass.getCourseCode().startsWith("COURSE-")) {

            logger.info("Course details missing or generic, fetching complete course details for courseId: {}",
                    courseClass.getCourseId());

            try {
                // Re-fetch the course details to ensure we have the latest information
                Map<String, Object> courseDetails = microserviceClient.fetchCourseDetails(courseClass.getCourseId());

                if (courseDetails != null) {
                    // Update the courseClass object with the fetched details
                    if (courseDetails.containsKey("courseCode")) {
                        courseClass.setCourseCode((String) courseDetails.get("courseCode"));
                        logger.debug("Updated courseCode to {}", courseClass.getCourseCode());
                    }

                    if (courseDetails.containsKey("courseName")) {
                        courseClass.setCourseName((String) courseDetails.get("courseName"));
                        logger.debug("Updated courseName to {}", courseClass.getCourseName());
                    }
                } else {
                    logger.warn("Could not fetch course details for courseId: {}", courseClass.getCourseId());
                }
            } catch (Exception e) {
                logger.error("Error fetching course details: {}", e.getMessage(), e);
            }
        }
    }
}