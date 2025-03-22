package com.example.courseregistration.service;

import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.dto.CreateRegistrationDTO;
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
import java.util.List;
import java.util.ArrayList;

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
    
    public List<RegistrationDTO> createRegistration(CreateRegistrationDTO createRegistrationDTO) {
        List<Long> studentIds = createRegistrationDTO.getStudentIds();
        Long classId = createRegistrationDTO.getClassId();

        // Only generate groupRegistrationId for group registrations (more than one student)
        Long groupRegistrationId = studentIds.size() > 1 ? generateGroupRegistrationId() : null;

        // Check if all students exist
        for (Long studentId : studentIds) {
            validateStudentExists(studentId);
        }

        // Retrieve class details and current vacancy
        CourseClassDTO courseClass = getClassDetails(classId);
        int availableVacancy = courseClass.getVacancy();

        // Determine registration status based on vacancy availability:
        // If there is enough vacancy for all students, register them;
        // otherwise, mark them as waitlisted.
        String registrationStatus;
        if (studentIds.size() <= availableVacancy) {
            registrationStatus = "Registered";
            updateClassVacancyDirect(courseClass, courseClass.getVacancy() - studentIds.size());

        } else {
            registrationStatus = "Waitlisted";
            // No vacancy update since no seats are taken
        }

        // Create registrations for all students in the group
        List<RegistrationDTO> createdRegistrations = new ArrayList<>();
        for (Long studentId : studentIds) {
            Registration registration = new Registration();
            registration.setStudentId(studentId);
            registration.setClassId(classId);
            registration.setRegistrationStatus(registrationStatus);
            registration.setRegisteredAt(LocalDateTime.now());
            registration.setGroupRegistrationId(groupRegistrationId);

            Registration savedRegistration = courseRegistrationRepository.save(registration);
            createdRegistrations.add(mapToDTO(savedRegistration));
        }

        return createdRegistrations;
    }

    public RegistrationDTO updateIndividualRegistrationStatus(Long registrationId, String newStatus) {
        Registration registration = courseRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration with ID " + registrationId + " not found"));
        String oldStatus = registration.getRegistrationStatus();

        // If no change, return current registration
        if (oldStatus.equalsIgnoreCase(newStatus)) {
            return mapToDTO(registration);
        }

        CourseClassDTO courseClass = getClassDetails(registration.getClassId());
        int netDelta = 0;
        // Transition: Waitlisted -> Registered requires reducing vacancy by 1
        if ("Waitlisted".equalsIgnoreCase(oldStatus) && "Registered".equalsIgnoreCase(newStatus)) {
            netDelta = -1;
            validateVacancy(courseClass, 1);
        }
        // Transition: Registered -> Cancelled increases vacancy by 1
        else if ("Registered".equalsIgnoreCase(oldStatus) && "Cancelled".equalsIgnoreCase(newStatus)) {
            netDelta = 1;
        }

        // Update the class vacancy accordingly
        int updatedVacancy = courseClass.getVacancy() + netDelta;
        updateClassVacancyDirect(courseClass, updatedVacancy);

        registration.setRegistrationStatus(newStatus);
        Registration updatedRegistration = courseRegistrationRepository.save(registration);
        return mapToDTO(updatedRegistration);
    }


    public List<RegistrationDTO> updateGroupRegistrationStatus(Long groupRegistrationId, String newStatus) {
        // Assumes that the repository provides this method.
        List<Registration> registrations = courseRegistrationRepository.findByGroupRegistrationId(groupRegistrationId);
        if (registrations.isEmpty()) {
            throw new RuntimeException("No registrations found for groupRegistrationId: " + groupRegistrationId);
        }
        
        // Assume all registrations in the group belong to the same class.
        Long classId = registrations.get(0).getClassId();
        CourseClassDTO courseClass = getClassDetails(classId);
        
        int countToRegister = 0;
        int countToCancel = 0;
        // Determine how many registrations are changing status.
        for (Registration registration : registrations) {
            String currentStatus = registration.getRegistrationStatus();
            if ("Waitlisted".equalsIgnoreCase(currentStatus) && "Registered".equalsIgnoreCase(newStatus)) {
                countToRegister++;
            } else if ("Registered".equalsIgnoreCase(currentStatus) && "Cancelled".equalsIgnoreCase(newStatus)) {
                countToCancel++;
            }
        }
        
        // Calculate net change: negative means seats will be filled; positive means seats become available.
        int netDelta = countToCancel - countToRegister;
        if (netDelta < 0) {
            validateVacancy(courseClass, -netDelta);
        }
        int updatedVacancy = courseClass.getVacancy() + netDelta;
        updateClassVacancyDirect(courseClass, updatedVacancy);

        List<RegistrationDTO> updatedRegistrations = new ArrayList<>();
        for (Registration registration : registrations) {
            registration.setRegistrationStatus(newStatus);
            Registration savedRegistration = courseRegistrationRepository.save(registration);
            updatedRegistrations.add(mapToDTO(savedRegistration));
        }
        return updatedRegistrations;
    }


    private void validateStudentExists(Long studentId) {
        String studentUrl = "http://localhost:8085/api/students/{studentId}";
        try {
            ResponseEntity<String> studentResponse = restTemplate.getForEntity(studentUrl, String.class, studentId);
            if (!studentResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Student with ID " + studentId + " does not exist");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Student with ID " + studentId + " does not exist");
            } else {
                throw e;
            }
        }
    }

    private CourseClassDTO getClassDetails(Long classId) {
        String getUrl = "http://localhost:8081/api/classes/{classId}";
        CourseClassDTO courseClass = restTemplate.getForObject(getUrl, CourseClassDTO.class, classId);

        if (courseClass == null) {
            throw new RuntimeException("Class not found in course management service.");
        }

        return courseClass;
    }

    private void validateVacancy(CourseClassDTO courseClass, int requiredVacancy) {
        if (courseClass.getVacancy() < requiredVacancy) {
            throw new RuntimeException("Not enough vacancy for all students in the group.");
        }
    }

    private void updateClassVacancyDirect(CourseClassDTO courseClass, int newVacancy) {
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("courseId", courseClass.getCourseId());
        updatePayload.put("dayOfWeek", courseClass.getDayOfWeek());
        updatePayload.put("startTime", courseClass.getStartTime().toString());
        updatePayload.put("endTime", courseClass.getEndTime().toString());
        updatePayload.put("maxCapacity", courseClass.getMaxCapacity());
        updatePayload.put("vacancy", newVacancy);

        String updateUrl = "http://localhost:8081/api/classes/{classId}";
        restTemplate.put(updateUrl, updatePayload, courseClass.getClassId());
    }

    private Long generateGroupRegistrationId() {
        Long maxGroupId = courseRegistrationRepository.findMaxGroupRegistrationId();
        return (maxGroupId == null) ? 1L : maxGroupId + 1;
    }


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
