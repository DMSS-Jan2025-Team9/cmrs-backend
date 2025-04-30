package com.example.courseregistration.service.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.StudentDTO;

//Helper function to interact with Microservices
@Component
public class MicroserviceClient {
    private final RestTemplate rest = new RestTemplate();
    private static final String classAPI = "http://localhost:8081/api/classSchedule/classId/{classId}";
    private static final String studentAPI = "http://localhost:8085/api/students//studentFullId/{studentFullId}";

    public CourseClassDTO fetchClass(Long classId) {
    try {
        CourseClassDTO dto =
            rest.getForObject(classAPI, CourseClassDTO.class, classId);

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Class not found: " + classId);
        }
        return dto;
    } catch (HttpClientErrorException.NotFound e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Class not found: " + classId);
    }
    }   

    public void updateVacancy(CourseClassDTO dto, int newVacancy) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("courseId",   dto.getCourseId());
        payload.put("dayOfWeek",  dto.getDayOfWeek());
        payload.put("startTime",  dto.getStartTime().toString());
        payload.put("endTime",    dto.getEndTime().toString());
        payload.put("maxCapacity",dto.getMaxCapacity());
        payload.put("vacancy",    newVacancy);

        rest.put(classAPI, payload, dto.getClassId());
    }

    public void validateStudentExists(String studentFullId) {
        try {
            rest.getForEntity(studentAPI, String.class, studentFullId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student with Full Id " + studentFullId + " does not exist"
                );
            }
            throw e;
        }
    }

    public StudentDTO fetchStudentByFullId(String studentFullId) {
        try {
            StudentDTO dto = rest.getForObject(
                studentAPI,       
                StudentDTO.class,
                studentFullId
            );

            if (dto == null) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student not found: " + studentFullId
                );
            }
            return dto;

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Student not found: " + studentFullId
            );
        }
    }


}
