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

        // Ensure we have course details
        String courseCode = courseClass.getCourseCode();
        String courseName = courseClass.getCourseName();

        if (courseCode == null || courseName == null) {
            logger.warn("Course details missing in courseClass object. CourseId: {}", courseClass.getCourseId());
            courseCode = courseCode != null ? courseCode : "COURSE-" + courseClass.getCourseId();
            courseName = courseName != null ? courseName : "Course " + courseClass.getCourseId();
        }

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
     * Overloaded method for backward compatibility
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

        // Ensure we have course details
        String courseCode = courseClass.getCourseCode();
        String courseName = courseClass.getCourseName();

        if (courseCode == null || courseName == null) {
            logger.warn("Course details missing in courseClass object. CourseId: {}", courseClass.getCourseId());
            courseCode = courseCode != null ? courseCode : "COURSE-" + courseClass.getCourseId();
            courseName = courseName != null ? courseName : "Course " + courseClass.getCourseId();
        }

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
     * Overloaded method for backward compatibility
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
}