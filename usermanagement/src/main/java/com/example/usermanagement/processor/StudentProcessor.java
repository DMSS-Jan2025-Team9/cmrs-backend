package com.example.usermanagement.processor;

import com.example.usermanagement.factory.UserFactory;
import com.example.usermanagement.model.*;
import com.example.usermanagement.repository.PermissionRepository;
import com.example.usermanagement.repository.RoleRepository;
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

import java.util.HashSet;
import java.util.List;

@Component
@StepScope
public class StudentProcessor implements ItemProcessor<Student, Student> {

    private final StudentValidationChain validationChain;

    private final NameCleaningStrategy nameCleaningStrategy; // Name cleaning strategy

    private final UserService userService;

    private final RestTemplate restTemplate;
    private final String programApiUrl = "http://localhost:8081/api/program/";
    // Fetch jobId from parameters
    private String jobId;

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;


    // Constructor to initialize the validation chain and name cleaning strategy
    public StudentProcessor(NameCleaningStrategy nameCleaningStrategy, UserService userService,@Value("#{jobParameters['jobId']}") String jobId,
                            PermissionRepository permissionRepository,RoleRepository roleRepository) {
        // Initialize the validation chain with validators
        this.validationChain = new StudentValidationChain()
                .addHandler(new FirstNameValidator())
                .addHandler(new LastNameValidator());

        this.jobId = jobId;
        // Initialize the name cleaning strategy
        this.nameCleaningStrategy = nameCleaningStrategy;
        this.userService = userService;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Student process(Student student) throws Exception {
        // Log the student details (for debugging)
        System.out.println("Processing student: " + student);
        student.setJobId(jobId); // Set the jobId before writing to DB

        // Validate using the chain
        validationChain.validate(student);

        // Clean and capitalize first and last names
        if (student.getFirstName() != null) {
            student.setFirstName(nameCleaningStrategy.clean(student.getFirstName())); // Clean and capitalize first name
        }

        if (student.getLastName() != null) {
            student.setLastName(nameCleaningStrategy.clean(student.getLastName())); // Clean and capitalize last name
        }

        // Combine cleaned names if both are present
        if (!student.getFirstName().trim().isEmpty() && !student.getLastName().trim().isEmpty()) {
            student.setName(student.getFirstName().trim() + " " + student.getLastName().trim());
        }

        // Fetch the program name using the programId (assuming it's in the student object)
        Long programId = student.getProgramId(); // Get the program ID from the student

        if (programId != null) {
            // Construct the URL with the programId to fetch program details
            String url = UriComponentsBuilder.fromHttpUrl(programApiUrl)
                    .pathSegment(programId.toString())
                    .toUriString();

            // Fetch the program details from the API (assuming you have a ProgramDto or similar)
            ProgramResponse programResponse = restTemplate.getForObject(url, ProgramResponse.class);

            // Set the program name in the student
            if (programResponse != null) {
                student.setProgramName(programResponse.getProgramName());
            }
        }

        // **Save the student to generate the studentId**
        student = userService.saveStudent(student); // Assuming a method in userService that saves the student entity and generates studentId.

        // **Generate the full student ID (e.g., "S12345") after saving**
        if (student.getStudentId() != null) {
            // Get the base student ID (e.g., from the database, S4)
            String baseStudentId = student.getStudentId().toString();

            // Generate a random 3-digit number (between 100 and 999)
            int randomPadding = 100 + (int)(Math.random() * 900); // Random number between 100 and 999

            // Combine base ID and random number
            String fullStudentId = "U" + baseStudentId + randomPadding;

            // Ensure the final ID is no longer than 5 digits (excluding the "S")
            if (fullStudentId.length() > 6) {
                // If length exceeds 6, trim the random padding appropriately
                int excessLength = fullStudentId.length() - 6;
                fullStudentId = fullStudentId.substring(0, fullStudentId.length() - excessLength);
            }

            // Set the final full ID with the prefix "S" and adjusted padding
            student.setStudentFullId(fullStudentId);
        }

        // **Create User for the Student**
        Role studentRole = new Role(); // Assuming you have a default student role
        studentRole.setDescription("Regular user who can view and register for courses");
        studentRole.setRoleName("student"); // Set the role name

        // Fetch only 'view_course' and 'register_course' permissions
        List<Permission> studentPermissions = permissionRepository.findByPermissionNameIn(
                List.of("view_course", "register_course")
        );

        // Assign to role
        studentRole.setPermissions(new HashSet<>(studentPermissions));

        // Save the role (if it’s new or you want to update permissions)
        roleRepository.save(studentRole);

        // Use the UserFactory to create and save the user
        User user = UserFactory.createUser(student, studentRole);

        // Save the user and role using UserService
        userService.saveUserWithRole(user, studentRole);

        // Link the saved user to the student
        student.setUser(user);

        // Save student again with user_id
        student = userService.saveStudent(student);

        // Log the student details after processing
        System.out.println("After processing student: " + student);

        // Log user creation
        System.out.println("Created user: " + user);

        return student;
    }
}
