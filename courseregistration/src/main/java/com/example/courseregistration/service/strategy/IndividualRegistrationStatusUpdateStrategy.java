package com.example.courseregistration.service.strategy;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.service.client.MicroserviceClient;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class IndividualRegistrationStatusUpdateStrategy implements RegistrationStatusUpdateStrategy {
    private final CourseRegistrationRepository repo;
    private final MicroserviceClient microserviceClient;
    private final RestTemplate rest;

    public IndividualRegistrationStatusUpdateStrategy(
        CourseRegistrationRepository repo,
        MicroserviceClient microserviceClient
        ){
        this.repo = repo;
        this.microserviceClient = microserviceClient;
        this.rest = new RestTemplate();
    }

    @Override
    public boolean supports(UpdateRegistrationStatusDTO dto) {
        return dto.getIdentifier() == 1;
    }

    @Override
    public List<RegistrationDTO> update(UpdateRegistrationStatusDTO dto) {
        Registration reg = repo.findById(dto.getId())
            .orElseThrow(() -> new RuntimeException("Registration ID " + dto.getId() + " not found"));

        String oldStatus = reg.getRegistrationStatus();
        String newStatus = dto.getNewStatus();
        if (oldStatus.equalsIgnoreCase(newStatus)) {
            return Collections.singletonList(mapToDTO(reg));
        }

        //Fetch Class details
        var course = microserviceClient.fetchClass(reg.getClassId());
        int delta = 0;
        if ("Waitlisted".equalsIgnoreCase(oldStatus) && "Registered".equalsIgnoreCase(newStatus)) {
            delta = -1;
            if (course.getVacancy() < 1) throw new RuntimeException("Not enough vacancy");
        } else if ("Registered".equalsIgnoreCase(oldStatus)
                   && ("Cancelled".equalsIgnoreCase(newStatus) || "Unenrolled".equalsIgnoreCase(newStatus))) {
            delta = 1;
        }

        microserviceClient.updateVacancy(course, course.getVacancy() + delta);

        // Save updated registration
        reg.setRegistrationStatus(newStatus);
        Registration updated = repo.save(reg);
        return Collections.singletonList(mapToDTO(updated));
    }

    private RegistrationDTO mapToDTO(Registration r) {
        return new RegistrationDTO(
            r.getRegistrationId(), r.getStudentId(), r.getClassId(),
            r.getRegisteredAt(), r.getRegistrationStatus(), r.getGroupRegistrationId()
        );
    }
}