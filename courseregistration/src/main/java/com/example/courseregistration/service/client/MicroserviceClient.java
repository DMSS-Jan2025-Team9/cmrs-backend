package com.example.courseregistration.service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.StudentDTO;
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
    private static final String classAPI = "http://coursemanagement-service:8081/api/classes/{classId}";
    private static final String courseAPI = "http://coursemanagement-service:8081/api/courses/{courseId}";

    private static final String studentAPI = "http://usermanagement-service:8085/api/students/studentFullId/{studentFullId}";
    private static final String studentIdAPI = "http://usermanagement-service:8085/api/students/{studentId}";
    private static final String courseDetailsAPI = "http://coursemanagement-service:8081/api/courses/courseId/{courseId}";

    public CourseClassDTO fetchClass(Long classId) {
        try {
            logger.debug("Fetching class with ID: {}", classId);
            CourseClassDTO dto = rest.getForObject(classAPI, CourseClassDTO.class, classId);

            if (dto == null) {
                logger.error("Class not found: {}", classId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Class not found: " + classId);
            }

            // If course details are missing, fetch them
            if (dto.getCourseCode() == null || dto.getCourseName() == null) {
                logger.debug("Course details missing, fetching course with ID: {}", dto.getCourseId());
                try {
                    // Fetch the course details
                    Map<String, Object> courseDetails = fetchCourseDetails(dto.getCourseId());

                    if (courseDetails != null) {
                        // Set the courseCode and courseName
                        if (courseDetails.containsKey("courseCode")) {
                            dto.setCourseCode((String) courseDetails.get("courseCode"));
                        } else {
                            dto.setCourseCode("COURSE-" + dto.getCourseId()); // Fallback
                        }

                        if (courseDetails.containsKey("courseName")) {
                            dto.setCourseName((String) courseDetails.get("courseName"));
                        } else {
                            dto.setCourseName("Course " + dto.getCourseId()); // Fallback
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error fetching course details: {}", e.getMessage());
                    // Set default values if course fetch fails
                    dto.setCourseCode("COURSE-" + dto.getCourseId());
                    dto.setCourseName("Course " + dto.getCourseId());
                }
            }

            logger.debug("Fetched class: {}", dto);
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

    public void validateStudentExists(String studentFullId) {
        try {
            rest.getForEntity(studentAPI, String.class, studentFullId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student with Full Id " + studentFullId + " does not exist");
            }
            throw e;
        }
    }

    public StudentDTO fetchStudentByFullId(String studentFullId) {
        try {
            StudentDTO dto = rest.getForObject(
                    studentAPI,
                    StudentDTO.class,
                    studentFullId);

            if (dto == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found: " + studentFullId);
            }
            return dto;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student not found: " + studentFullId);
        }
    }

    public StudentDTO fetchStudentById(Long studentId) {
        try {
            logger.debug("Fetching student with ID: {}", studentId);
            StudentDTO dto = rest.getForObject(
                    studentIdAPI,
                    StudentDTO.class,
                    studentId);

            if (dto == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found with ID: " + studentId);
            }
            return dto;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student not found with ID: " + studentId);
        }
    }

    /**
     * Fetches detailed course information by courseId
     *
     * @param courseId the ID of the course to fetch
     * @return Map containing course details or null if not found
     */
    public Map<String, Object> fetchCourseDetails(Long courseId) {
        try {
            logger.debug("Fetching course details for courseId: {}", courseId);
            Map<String, Object> courseDetails = rest.getForObject(
                    courseDetailsAPI,
                    Map.class,
                    courseId);

            if (courseDetails == null) {
                logger.warn("No course details found for courseId: {}", courseId);
                return null;
            }

            logger.debug("Fetched course details: {}", courseDetails);
            return courseDetails;

        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Course not found with ID: {}", courseId);
            return null;
        } catch (Exception e) {
            logger.error("Error fetching course details for courseId {}: {}", courseId, e.getMessage());
            return null;
        }
    }
}
