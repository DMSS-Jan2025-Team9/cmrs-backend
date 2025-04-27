package com.example.courseregistration.service.strategy;

import com.example.courseregistration.dto.CreateRegistrationDTO;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import com.example.courseregistration.service.NotificationPublisherService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.example.courseregistration.service.client.MicroserviceClient;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndividualRegistrationCreationStrategy implements RegistrationCreationStrategy {
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final MicroserviceClient microserviceClient;
    private final RestTemplate restTemplate;
    private final NotificationPublisherService notificationPublisherService;

    public IndividualRegistrationCreationStrategy(
            CourseRegistrationRepository courseRegistrationRepository,
            MicroserviceClient microserviceClient,
            NotificationPublisherService notificationPublisherService) {
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.microserviceClient = microserviceClient;
        this.restTemplate = new RestTemplate();
        this.notificationPublisherService = notificationPublisherService;
    }

    @Override
    public boolean supports(CreateRegistrationDTO dto) {
        return dto.getStudentIds().size() == 1;
    }

    @Override
    public List<RegistrationDTO> create(CreateRegistrationDTO dto) {
        Long studentId = dto.getStudentIds().get(0);

        // Validate student exists
        microserviceClient.validateStudentExists(studentId);

        // Fetch class details
        CourseClassDTO courseClass = microserviceClient.fetchClass(dto.getClassId());

        // Determine status and adjust vacancy
        String status;
        if (courseClass.getVacancy() >= 1) {
            status = "Registered";
            microserviceClient.updateVacancy(courseClass, courseClass.getVacancy() - 1);
        } else {
            status = "Waitlisted";
            // Send waitlist notification
            notificationPublisherService.publishWaitlistNotification(studentId, courseClass);
        }

        // Create and save registration
        Registration reg = new Registration();
        reg.setStudentId(studentId);
        reg.setClassId(dto.getClassId());
        reg.setRegisteredAt(LocalDateTime.now());
        reg.setRegistrationStatus(status);
        reg.setGroupRegistrationId(null);

        Registration saved = courseRegistrationRepository.save(reg);
        RegistrationDTO result = new RegistrationDTO(
                saved.getRegistrationId(),
                saved.getStudentId(),
                saved.getClassId(),
                saved.getRegisteredAt(),
                saved.getRegistrationStatus(),
                saved.getGroupRegistrationId());

        return Collections.singletonList(result);
    }
}
