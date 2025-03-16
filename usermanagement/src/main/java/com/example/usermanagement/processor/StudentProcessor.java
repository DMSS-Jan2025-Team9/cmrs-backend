package com.example.usermanagement.processor;

import com.example.usermanagement.dto.ProgramResponse;
import com.example.usermanagement.dto.Role;
import com.example.usermanagement.dto.Student;
import com.example.usermanagement.dto.User;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.strategy.NameCleaningStrategy;
import com.example.usermanagement.validation.FirstNameValidator;
import com.example.usermanagement.validation.LastNameValidator;
import com.example.usermanagement.validation.StudentValidationChain;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class StudentProcessor implements ItemProcessor<Student, Student> {
//    @Override
//    public Student process(Student student) throws Exception {
//        return student;
//    }

//    @Override
//    public Student process(Student student) throws Exception {
//        // Log the student details (for debugging purposes)
//        System.out.println("Processing student: " + student);
//
//        // Perform some validation (example: check if the student has a valid name)
//        if (student.getName() == null || student.getName().isEmpty()) {
//            throw new IllegalArgumentException("Student name cannot be empty");
//        }
//
//        // Example transformation (e.g., capitalize name)
//        String name = student.getName();
//        student.setName(name != null ? name.toUpperCase() : name);
//
//        // You can add more transformations or actions here...
//
//        return student;  // Return the processed student
//    }

    private final StudentValidationChain validationChain;

    private final NameCleaningStrategy nameCleaningStrategy; // Name cleaning strategy

    private final UserService userService;

    private final RestTemplate restTemplate;
    private final String programApiUrl = "http://localhost:8081/api/program/";


    // Constructor to initialize the validation chain and name cleaning strategy
    public StudentProcessor(NameCleaningStrategy nameCleaningStrategy, UserService userService) {
        // Initialize the validation chain with validators
        this.validationChain = new StudentValidationChain()
                .addHandler(new FirstNameValidator())
                .addHandler(new LastNameValidator());

        // Initialize the name cleaning strategy
        this.nameCleaningStrategy = nameCleaningStrategy;
        this.userService = userService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Student process(Student student) throws Exception {
        // Log the student details (for debugging)
        System.out.println("Processing student: " + student);

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
                // Trim the random padding if the length exceeds 6 characters (including "S")
                fullStudentId = "U" + baseStudentId.substring(0, 5 - randomPadding);
            }

            // Set the final full ID with the prefix "S" and adjusted padding
            student.setStudentFullId(fullStudentId);
        }

        // **Create User for the Student**
        Role studentRole = new Role(); // Assuming you have a default student role
        studentRole.setRoleName("student"); // Set the role name

        User user = userService.createAndSaveUser(student, studentRole);


        // Log the student details after processing
        System.out.println("After processing student: " + student);


        // Log user creation
        System.out.println("Created user: " + user);



        return student;
    }
}
