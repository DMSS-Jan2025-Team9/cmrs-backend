package com.example.courseregistration.service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.example.courseregistration.dto.CourseClassDTO;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

//Helper function to interact with Microservices
@Component
public class MicroserviceClient {
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceClient.class);

    private final RestTemplate rest = new RestTemplate();
    // private static final String classAPI =
    // "http://localhost:8081/api/classes/{classId}";
    private static final String classAPI = "http://coursemanagement-service:8081/api/classes/{classId}"; // For Docker
                                                                                                         // environment
    private static final String courseAPI = "http://coursemanagement-service:8081/api/courses/courseId/{courseId}"; // Updated
                                                                                                                    // API
                                                                                                                    // endpoint
                                                                                                                    // to
                                                                                                                    // get
                                                                                                                    // course
                                                                                                                    // details
    private static final String studentAPI = "http://usermanagement-service:8085/api/students/{studentId}"; // Updated
                                                                                                            // for
                                                                                                            // Docker
                                                                                                            // environment

    public CourseClassDTO fetchClass(Long classId) {
        try {
            logger.debug("Fetching class with ID: {}", classId);
            CourseClassDTO dto = rest.getForObject(classAPI, CourseClassDTO.class, classId);

            if (dto == null) {
                logger.error("Class not found: {}", classId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Class not found: " + classId);
            }

            // If course details are missing, fetch them explicitly
            if (dto.getCourseCode() == null || dto.getCourseName() == null) {
                logger.debug("Course details missing, fetching course with ID: {}", dto.getCourseId());
                try {
                    // Fetch the course details from the correct endpoint
                    Map<String, Object> courseDetails = rest.getForObject(courseAPI, Map.class, dto.getCourseId());
                    logger.debug("Fetched course details: {}", courseDetails);

                    if (courseDetails != null) {
                        // Set the courseCode and courseName
                        if (courseDetails.containsKey("courseCode")) {
                            dto.setCourseCode((String) courseDetails.get("courseCode"));
                            logger.debug("Set courseCode to {}", dto.getCourseCode());
                        } else {
                            dto.setCourseCode("COURSE-" + dto.getCourseId()); // Fallback
                        }

                        if (courseDetails.containsKey("courseName")) {
                            dto.setCourseName((String) courseDetails.get("courseName"));
                            logger.debug("Set courseName to {}", dto.getCourseName());
                        } else {
                            dto.setCourseName("Course " + dto.getCourseId()); // Fallback
                        }
                    } else {
                        logger.warn("Course details response was null for course ID: {}", dto.getCourseId());
                        // Set fallback values
                        dto.setCourseCode("COURSE-" + dto.getCourseId());
                        dto.setCourseName("Course " + dto.getCourseId());
                    }
                } catch (Exception e) {
                    logger.error("Error fetching course details: {}", e.getMessage(), e);
                    // Set default values if course fetch fails
                    dto.setCourseCode("COURSE-" + dto.getCourseId());
                    dto.setCourseName("Course " + dto.getCourseId());
                }
            }

            logger.debug("Returning class DTO: {}", dto);
            return dto;
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Class not found: {}", classId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Class not found: " + classId);
        }
    }

    public void updateVacancy(CourseClassDTO dto, int newVacancy) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("courseId", dto.getCourseId());
        payload.put("dayOfWeek", dto.getDayOfWeek());
        payload.put("startTime", dto.getStartTime().toString());
        payload.put("endTime", dto.getEndTime().toString());
        payload.put("maxCapacity", dto.getMaxCapacity());
        payload.put("vacancy", newVacancy);

        rest.put(classAPI, payload, dto.getClassId());
    }

    public void validateStudentExists(Long studentId) {
        try {
            rest.getForEntity(studentAPI, String.class, studentId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student with ID " + studentId + " does not exist");
            }
            throw e;
        }
    }
}
