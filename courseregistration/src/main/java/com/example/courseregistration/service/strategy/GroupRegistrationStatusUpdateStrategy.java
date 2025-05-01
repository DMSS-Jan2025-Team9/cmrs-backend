package com.example.courseregistration.service.strategy;

import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.courseregistration.service.client.MicroserviceClient;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.courseregistration.service.NotificationPublisherService;
import com.example.courseregistration.service.WaitlistNotificationService;
import com.example.courseregistration.dto.StudentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GroupRegistrationStatusUpdateStrategy implements RegistrationStatusUpdateStrategy {
    private static final Logger logger = LoggerFactory.getLogger(GroupRegistrationStatusUpdateStrategy.class);

    private final CourseRegistrationRepository repo;
    private final MicroserviceClient microserviceClient;
    private final RestTemplate rest;
    private final NotificationPublisherService notificationPublisherService;
    private final WaitlistNotificationService waitlistNotificationService;

    public GroupRegistrationStatusUpdateStrategy(
            CourseRegistrationRepository repo,
            MicroserviceClient microserviceClient,
            NotificationPublisherService notificationPublisherService,
            WaitlistNotificationService waitlistNotificationService) {
        this.repo = repo;
        this.microserviceClient = microserviceClient;
        this.rest = new RestTemplate();
        this.notificationPublisherService = notificationPublisherService;
        this.waitlistNotificationService = waitlistNotificationService;
    }

    @Override
    public boolean supports(UpdateRegistrationStatusDTO dto) {
        return dto.getIdentifier() == 2;
    }

    @Override
    public List<RegistrationDTO> update(UpdateRegistrationStatusDTO dto) {
        List<Registration> regs = repo.findByGroupRegistrationId(dto.getId());
        if (regs.isEmpty())
            throw new RuntimeException("Group ID " + dto.getId() + " not found");

        CourseClassDTO c = microserviceClient.fetchClass(regs.get(0).getClassId());

        logger.debug("Updating group registration status to {} for group ID {}", dto.getNewStatus(), dto.getId());

        // compute net delta and validate
        int toRegister = (int) regs.stream()
                .filter(r -> "Waitlisted".equalsIgnoreCase(r.getRegistrationStatus()))
                .count();
        int toCancel = (int) regs.stream()
                .filter(r -> "Registered".equalsIgnoreCase(r.getRegistrationStatus()))
                .count();
        int delta = ("Registered".equalsIgnoreCase(dto.getNewStatus()) ? -toRegister : 0)
                + ("Cancelled".equalsIgnoreCase(dto.getNewStatus()) ? toCancel : 0);

        if (delta < 0 && c.getVacancy() < -delta)
            throw new RuntimeException("Not enough seats");

        // Update vacancy
        microserviceClient.updateVacancy(c, c.getVacancy() + delta);

        // If vacancies are being created, notify waitlisted students
        if (delta > 0) {
            logger.info("Vacancies increased by {} for class {}, notifying waitlisted students", delta, c.getClassId());
            waitlistNotificationService.notifyWaitlistedStudents(c.getClassId(), c);
        }

        // apply update
        regs.forEach(r -> r.setRegistrationStatus(dto.getNewStatus()));
        repo.saveAll(regs);

        return regs.stream()
                .map(r -> new RegistrationDTO(
                        r.getRegistrationId(), r.getStudentId(), r.getClassId(),
                        r.getRegisteredAt(), r.getRegistrationStatus(), r.getGroupRegistrationId()))
                .collect(Collectors.toList());
    }
}