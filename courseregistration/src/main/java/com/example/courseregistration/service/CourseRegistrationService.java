package com.example.courseregistration.service;

import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class CourseRegistrationService {

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public CourseRegistrationService(CourseRegistrationRepository courseRegistrationRepository) {
        this.courseRegistrationRepository = courseRegistrationRepository;
    }

    public List<RegistrationDTO> filterRegistration(Long registrationId, Long studentId, Long classId, String registrationStatus, Long groupRegistrationId) {
        List<Registration> registrations = courseRegistrationRepository.filterRegistration(
                registrationId, studentId, classId, registrationStatus, groupRegistrationId);
        return registrations.stream()
                .map(register -> new RegistrationDTO(
                        register.getRegistrationId(),
                        register.getStudentId(),
                        register.getClassId(),
                        register.getRegisteredAt(),
                        register.getRegistrationStatus(),
                        register.getGroupRegistrationId()
                ))
                .collect(Collectors.toList());
    }
    
    public RegistrationDTO createRegistration(RegistrationDTO registrationDTO) {

        //check if student exists
        String studentUrl = "http://localhost:8085/api/students/{studentId}";
        try {
            ResponseEntity<String> studentResponse = restTemplate.getForEntity(studentUrl, String.class, registrationDTO.getStudentId());
            if (!studentResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Student id does not exists");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Student id does not exists");
            } else {
                throw e;
            }
        }
        Long classId = registrationDTO.getClassId();

        // Query the course management microservice for class details.
        String getUrl = "http://localhost:8081/api/classes/{classId}";
        CourseClassDTO courseClass = restTemplate.getForObject(getUrl, CourseClassDTO.class, classId);

        if (courseClass == null) {
            throw new RuntimeException("Class not found in course management service.");
        }

        String registrationStatus;
        // If vacancy is available, update the vacancy and set status to "Registered"
        if (courseClass.getVacancy() > 0) {
            int newVacancy = courseClass.getVacancy() - 1;
            // Build full update payload with all required fields.
            Map<String, Object> updatePayload = new HashMap<>();
            updatePayload.put("courseId", courseClass.getCourseId());
            updatePayload.put("dayOfWeek", courseClass.getDayOfWeek());
            updatePayload.put("startTime", courseClass.getStartTime().toString());
            updatePayload.put("endTime", courseClass.getEndTime().toString());
            updatePayload.put("maxCapacity", courseClass.getMaxCapacity());
            updatePayload.put("vacancy", newVacancy);

            String updateUrl = "http://localhost:8081/api/classes/{classId}";
            restTemplate.put(updateUrl, updatePayload, classId);

            registrationStatus = "Registered";
        } else {
            // If no vacancy, mark registration as "Waitlisted"
            registrationStatus = "Waitlisted";
        }

        // Create registration with the appropriate status.
        Registration registration = mapToEntity(registrationDTO);
        registration.setRegistrationStatus(registrationStatus);
        // Set the current time as the registration time.
        registration.setRegisteredAt(LocalDateTime.now());
        Registration savedRegistration = courseRegistrationRepository.save(registration);
        return mapToDTO(savedRegistration);
    }
    
    // Helper method: Convert RegistrationDTO to Registration entity.
    private Registration mapToEntity(RegistrationDTO dto) {
        Registration registration = new Registration();
        registration.setStudentId(dto.getStudentId());
        registration.setClassId(dto.getClassId());
        // Set registeredAt to current time here if not set later
        // registration.setRegisteredAt(LocalDateTime.now());
        registration.setGroupRegistrationId(dto.getGroupRegistrationId());
        return registration;
    }
    
    // Helper method: Convert Registration entity to RegistrationDTO.
    private RegistrationDTO mapToDTO(Registration registration) {
        return new RegistrationDTO(
                registration.getRegistrationId(),
                registration.getStudentId(),
                registration.getClassId(),
                registration.getRegisteredAt(),
                registration.getRegistrationStatus(),
                registration.getGroupRegistrationId()
        );
    }
    
    private static class CourseClassDTO {
        private Long classId;
        private Long courseId;
        private String dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private int maxCapacity;
        private int vacancy;

        public CourseClassDTO() {
        }

        public Long getClassId() {
            return classId;
        }

        public void setClassId(Long classId) {
            this.classId = classId;
        }

        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }

        public String getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }

        public int getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        public int getVacancy() {
            return vacancy;
        }

        public void setVacancy(int vacancy) {
            this.vacancy = vacancy;
        }
    }
}
