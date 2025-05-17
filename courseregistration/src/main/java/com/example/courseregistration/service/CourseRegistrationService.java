package com.example.courseregistration.service;

import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.dto.CreateRegistrationDTO;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;
import com.example.courseregistration.service.strategy.RegistrationCreationStrategy;
import com.example.courseregistration.service.strategy.RegistrationStatusUpdateStrategy;
import com.example.courseregistration.service.client.MicroserviceClient;
import com.example.courseregistration.dto.CourseClassDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CourseRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(CourseRegistrationService.class);

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final List<RegistrationCreationStrategy> creationStrategies;
    private final List<RegistrationStatusUpdateStrategy> statusUpdateStrategies;
    private final MicroserviceClient microserviceClient;
    private final RestTemplate restTemplate = new RestTemplate();
    private final NotificationPublisherService notificationPublisherService;
    private final WaitlistNotificationService waitlistNotificationService;

    public CourseRegistrationService(
            CourseRegistrationRepository courseRegistrationRepository,
            List<RegistrationCreationStrategy> creationStrategies,
            List<RegistrationStatusUpdateStrategy> statusUpdateStrategies,
            MicroserviceClient microserviceClient,
            NotificationPublisherService notificationPublisherService,
            WaitlistNotificationService waitlistNotificationService) {
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.creationStrategies = creationStrategies;
        this.statusUpdateStrategies = statusUpdateStrategies;
        this.microserviceClient = microserviceClient;
        this.notificationPublisherService = notificationPublisherService;
        this.waitlistNotificationService = waitlistNotificationService;
    }

    public List<RegistrationDTO> filterRegistration(
            Long registrationId,
            Long studentId,
            Long classId,
            String registrationStatus,
            Long groupRegistrationId,
            Boolean groupRegistration) {
        List<Registration> regs = courseRegistrationRepository.filterRegistration(
                registrationId, studentId, classId, registrationStatus, groupRegistrationId);

        if (groupRegistration != null) {
            regs = regs.stream()
                    .filter(r -> groupRegistration
                            ? r.getGroupRegistrationId() != null // true ⇒ keep those in a group
                            : r.getGroupRegistrationId() == null) // false ⇒ keep the singles
                    .collect(Collectors.toList());
        }

        return regs.stream()
                .map(r -> new RegistrationDTO(
                        r.getRegistrationId(),
                        r.getStudentId(),
                        r.getClassId(),
                        r.getRegisteredAt(),
                        r.getRegistrationStatus(),
                        r.getGroupRegistrationId()))
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
        logger.debug("Unenrolling registration with ID: {}", registrationId);

        // Retrieve the registration; throw an exception if not found.
        Registration registration = courseRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration with ID " + registrationId + " not found"));

        String oldStatus = registration.getRegistrationStatus();

        // If the registration is already unenrolled, simply return it.
        if (oldStatus.equalsIgnoreCase("Unenrolled")) {
            logger.debug("Registration {} is already unenrolled", registrationId);
            return mapToDTO(registration);
        }

        // Retrieve the class details.
        CourseClassDTO courseClass = microserviceClient.fetchClass(registration.getClassId());

        // If the student was registered (occupying a seat), update the vacancy.
        if ("Registered".equalsIgnoreCase(oldStatus)) {
            int updatedVacancy = courseClass.getVacancy() + 1;
            logger.debug("Updating vacancy for class {} to {}", registration.getClassId(), updatedVacancy);
            microserviceClient.updateVacancy(courseClass, updatedVacancy);

            // Notify waitlisted students about the vacancy
            waitlistNotificationService.notifyWaitlistedStudents(registration.getClassId(), courseClass);
        }

        // Update the registration status to "Unenrolled".
        registration.setRegistrationStatus("Unenrolled");
        Registration updatedRegistration = courseRegistrationRepository.save(registration);
        logger.info("Registration {} successfully unenrolled", registrationId);
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
                registration.getGroupRegistrationId());
    }

}
