package com.example.usermanagement.processor;

import com.example.usermanagement.factory.UserFactory;
import com.example.usermanagement.model.*;
import com.example.usermanagement.repository.UserRoleRepository;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.strategy.NameCleaningStrategy;
import com.example.usermanagement.validation.FirstNameValidator;
import com.example.usermanagement.validation.LastNameValidator;
import com.example.usermanagement.validation.StudentValidationChain;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@StepScope
public class StudentProcessor implements ItemProcessor<Student, Student> {

    private final StudentValidationChain validationChain;
    private final NameCleaningStrategy nameCleaningStrategy;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final String programApiUrl = "http://localhost:8081/api/program/";
    private final String jobId;
    private final UserRoleRepository userRoleRepository;

    public StudentProcessor(NameCleaningStrategy nameCleaningStrategy,
                            UserService userService,
                            @Value("#{jobParameters['jobId']}") String jobId,
                            UserRoleRepository userRoleRepository) {
        this.validationChain = new StudentValidationChain()
                .addHandler(new FirstNameValidator())
                .addHandler(new LastNameValidator());
        this.nameCleaningStrategy = nameCleaningStrategy;
        this.userService = userService;
        this.userRoleRepository = userRoleRepository;
        this.restTemplate = new RestTemplate();
        this.jobId = jobId;
    }

    @Override
    public Student process(Student student) throws Exception {
        System.out.println("Processing student: " + student);
        student.setJobId(jobId);

        // 1. Clean and validate names
        validationChain.validate(student);
        student.setFirstName(nameCleaningStrategy.clean(student.getFirstName()));
        student.setLastName(nameCleaningStrategy.clean(student.getLastName()));
        student.setName(student.getFirstName().trim() + " " + student.getLastName().trim());

        // 2. Fetch program name
        if (student.getProgramId() != null) {
            String url = UriComponentsBuilder.fromHttpUrl(programApiUrl)
                    .pathSegment(student.getProgramId().toString())
                    .toUriString();
            ProgramResponse programResponse = restTemplate.getForObject(url, ProgramResponse.class);
            if (programResponse != null) {
                student.setProgramName(programResponse.getProgramName());
            }
        }

        // 3. Create and save User first
        User user = UserFactory.createUser(student);
        User savedUser = userService.saveUser(user);

        userRoleRepository.assignStudentRole(savedUser.getUserId());

        // 4. Attach user to student and save
        student.setUser(savedUser);
        student = userService.saveStudent(student);

        // 5. Generate studentFullId and update user email/username
        if (student.getStudentId() != null) {
            Long idLong = student.getStudentId();

            // Pad ID to 2 digits if it's less than 10
            String paddedId = (idLong < 10) ? String.format("%02d", idLong) : idLong.toString();

            int randomPadding = 1000 + (int) (Math.random() * 9000); // Generates 1000–9999
            String fullStudentId = "U" + paddedId + randomPadding;   // e.g., U010234 or U101234
            student.setStudentFullId(fullStudentId);

            savedUser.setUsername(fullStudentId);
            savedUser.setEmail(fullStudentId + "@university.edu");

            userService.saveUser(savedUser);
            student = userService.saveStudent(student);
        }

        System.out.println("Created user: " + savedUser);
        System.out.println("After processing student: " + student);
        return student;
    }
}
