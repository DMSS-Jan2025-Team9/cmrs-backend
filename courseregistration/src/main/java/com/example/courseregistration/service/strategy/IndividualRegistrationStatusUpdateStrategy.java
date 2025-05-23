package com.example.courseregistration.service.strategy;

import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import com.example.courseregistration.service.NotificationPublisherService;
import com.example.courseregistration.service.WaitlistNotificationService;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IndividualRegistrationStatusUpdateStrategy implements RegistrationStatusUpdateStrategy {
    private static final Logger logger = LoggerFactory.getLogger(IndividualRegistrationStatusUpdateStrategy.class);

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final MicroserviceClient microserviceClient;
    private final NotificationPublisherService notificationPublisherService;
    private final WaitlistNotificationService waitlistNotificationService;

    public IndividualRegistrationStatusUpdateStrategy(
            CourseRegistrationRepository courseRegistrationRepository,
            MicroserviceClient microserviceClient,
            NotificationPublisherService notificationPublisherService,
            WaitlistNotificationService waitlistNotificationService) {
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.microserviceClient = microserviceClient;
        this.notificationPublisherService = notificationPublisherService;
        this.waitlistNotificationService = waitlistNotificationService;
    }

    @Override
    public boolean supports(UpdateRegistrationStatusDTO dto) {
        return dto.getIdentifier() == 1;
    }

    @Override
    public List<RegistrationDTO> update(UpdateRegistrationStatusDTO dto) {
        Long registrationId = dto.getId();

        // Fetch registration and update status
        Optional<Registration> optReg = courseRegistrationRepository.findById(registrationId);
        if (!optReg.isPresent()) {
            throw new RuntimeException("Registration not found: " + registrationId);
        }

        Registration reg = optReg.get();
        CourseClassDTO courseClass = microserviceClient.fetchClass(reg.getClassId());

        // Process status changes and update vacancy
        String oldStatus = reg.getRegistrationStatus();
        String newStatus = dto.getNewStatus();

        // Check the status transitions
        if (oldStatus.equals(newStatus)) {
            // No change in status
            return Collections.singletonList(mapToDTO(reg));
        }

        // Handle cases where student is registered (status change to 'Registered')
        if (newStatus.equals("Registered") && !oldStatus.equals("Registered")) {
            // Decrease vacancy by 1
            if (courseClass.getVacancy() <= 0) {
                throw new RuntimeException("No vacancy available for class " + reg.getClassId());
            }
            microserviceClient.updateVacancy(courseClass, courseClass.getVacancy() - 1);
        }

        // Handle cases where student is un-registered (status change from 'Registered')
        if (oldStatus.equals("Registered") && !newStatus.equals("Registered")) {
            // Increase vacancy by 1
            microserviceClient.updateVacancy(courseClass, courseClass.getVacancy() + 1);

            // Notify waitlisted students if there is now a vacancy
            waitlistNotificationService.notifyWaitlistedStudents(reg.getClassId(), courseClass);
        }

        // Update the status and save
        reg.setRegistrationStatus(newStatus);
        Registration updatedReg = courseRegistrationRepository.save(reg);

        return Collections.singletonList(mapToDTO(updatedReg));
    }

    private RegistrationDTO mapToDTO(Registration reg) {
        return new RegistrationDTO(
                reg.getRegistrationId(),
                reg.getStudentId(),
                reg.getClassId(),
                reg.getRegisteredAt(),
                reg.getRegistrationStatus(),
                reg.getGroupRegistrationId());
    }
}