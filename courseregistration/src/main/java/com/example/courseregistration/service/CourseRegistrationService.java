package com.example.courseregistration.service;

import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
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
import com.example.courseregistration.service.strategy.RegistrationCreationStrategy;
import com.example.courseregistration.service.strategy.RegistrationStatusUpdateStrategy;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.example.courseregistration.dto.CourseClassDTO;

@Service
public class CourseRegistrationService {

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final List<RegistrationCreationStrategy> creationStrategies;
    private final List<RegistrationStatusUpdateStrategy> statusUpdateStrategies;
    private final MicroserviceClient microserviceClient;
    private final RestTemplate restTemplate = new RestTemplate();

    public CourseRegistrationService(
        CourseRegistrationRepository courseRegistrationRepository,
        List<RegistrationCreationStrategy> creationStrategies,
        List<RegistrationStatusUpdateStrategy> statusUpdateStrategies,
        MicroserviceClient microserviceClient) {
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.creationStrategies = creationStrategies;
        this.statusUpdateStrategies = statusUpdateStrategies;
        this.microserviceClient = microserviceClient;
    }
    
    public List<RegistrationDTO> filterRegistration(
            Long registrationId,
            Long studentId,
            Long classId,
            String registrationStatus,
            Long groupRegistrationId,
            Boolean groupRegistration   
    ) {
        List<Registration> regs = courseRegistrationRepository.filterRegistration(
            registrationId, studentId, classId, registrationStatus, groupRegistrationId
        );

        if (groupRegistration != null) {
            regs = regs.stream()
                .filter(r -> groupRegistration 
                        ? r.getGroupRegistrationId() != null    // true ⇒ keep those in a group
                        : r.getGroupRegistrationId() == null)   // false ⇒ keep the singles
                .collect(Collectors.toList());
        }

        return regs.stream()
            .map(r -> new RegistrationDTO(
                r.getRegistrationId(),
                r.getStudentId(),
                r.getClassId(),
                r.getRegisteredAt(),
                r.getRegistrationStatus(),
                r.getGroupRegistrationId()
            ))
            .collect(Collectors.toList());
    }

    public List<RegistrationDTO> createRegistration(CreateRegistrationDTO dto) {
        return creationStrategies.stream()
                .filter(s -> s.supports(dto))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported registration type"))
                .create(dto);
    }

    public List<RegistrationDTO> updateRegistrationStatus(UpdateRegistrationStatusDTO dto) {
        return statusUpdateStrategies.stream()
            .filter(s -> s.supports(dto))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported status update type"))
            .update(dto);
    }

    public RegistrationDTO unenrollRegistration(Long registrationId) {
        // Retrieve the registration; throw an exception if not found.
        Registration registration = courseRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration with ID " + registrationId + " not found"));

        String oldStatus = registration.getRegistrationStatus();

        // If the registration is already unenrolled, simply return it.
        if (oldStatus.equalsIgnoreCase("Unenrolled")) {
            return mapToDTO(registration);
        }

        // Retrieve the class details.
        CourseClassDTO courseClass = microserviceClient.fetchClass(registration.getClassId());

        // If the student was registered (occupying a seat), update the vacancy.
        if ("Registered".equalsIgnoreCase(oldStatus)) {
            int updatedVacancy = courseClass.getVacancy() + 1;
            microserviceClient.updateVacancy(courseClass, updatedVacancy);
        }
        
        // Update the registration status to "Unenrolled".
        registration.setRegistrationStatus("Unenrolled");
        Registration updatedRegistration = courseRegistrationRepository.save(registration);
        return mapToDTO(updatedRegistration);
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
    
}
