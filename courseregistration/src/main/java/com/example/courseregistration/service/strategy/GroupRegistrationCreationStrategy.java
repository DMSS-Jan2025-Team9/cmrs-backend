package com.example.courseregistration.service.strategy;

import com.example.courseregistration.dto.CreateRegistrationDTO;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.StudentDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.courseregistration.service.client.MicroserviceClient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupRegistrationCreationStrategy implements RegistrationCreationStrategy {
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final MicroserviceClient microserviceClient;
    private final RestTemplate restTemplate;

    public GroupRegistrationCreationStrategy(
        CourseRegistrationRepository courseRegistrationRepository,
        MicroserviceClient microserviceClient
        ) {
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.microserviceClient = microserviceClient;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean supports(CreateRegistrationDTO dto) {
        return dto.getStudentFullIds().size() > 1;
    }

    @Override
    public List<RegistrationDTO> create(CreateRegistrationDTO dto) {
        List<String> studentFullIds = dto.getStudentFullIds();
        List<Long> studentIds = new ArrayList<>();
        // Generate groupRegistrationId
        Long maxId = courseRegistrationRepository.findMaxGroupRegistrationId();
        Long groupId = (maxId == null) ? 1L : maxId + 1;

        for (String sid : studentFullIds) {
            microserviceClient.validateStudentExists(sid);
            StudentDTO student = microserviceClient.fetchStudentByFullId(sid);
            studentIds.add(student.getStudentId());
        }

        // Fetch class details
        CourseClassDTO course = microserviceClient.fetchClass(dto.getClassId());
        String status;
        if (studentIds.size() <= course.getVacancy()) {
            status = "Registered";
            microserviceClient.updateVacancy(course, course.getVacancy() - studentIds.size());
        } else {
            status = "Waitlisted";
        }


        // Create and save
        List<RegistrationDTO> results = new ArrayList<>();
        for (Long sid : studentIds) {
            Registration reg = new Registration();
            reg.setStudentId(sid);
            reg.setClassId(dto.getClassId());
            reg.setRegisteredAt(LocalDateTime.now());
            reg.setRegistrationStatus(status);
            reg.setGroupRegistrationId(groupId);

            Registration saved = courseRegistrationRepository.save(reg);
            results.add(new RegistrationDTO(
                saved.getRegistrationId(),
                saved.getStudentId(),
                saved.getClassId(),
                saved.getRegisteredAt(),
                saved.getRegistrationStatus(),
                saved.getGroupRegistrationId()));
        }

        return results;
    }


}
