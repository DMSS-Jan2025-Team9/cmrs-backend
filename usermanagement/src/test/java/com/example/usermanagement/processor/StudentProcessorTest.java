package com.example.usermanagement.processor;

import com.example.usermanagement.model.ProgramResponse;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRoleRepository;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.strategy.CompositeNameCleaningStrategy;
import com.example.usermanagement.strategy.NameCleaningStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentProcessorTest {

    @Mock
    private NameCleaningStrategy nameCleaningStrategy;

    @Mock
    private UserService userService;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RestTemplate restTemplate;

    private StudentProcessor studentProcessor;

    @BeforeEach
    public void setUp() {
        studentProcessor = new StudentProcessor(
                nameCleaningStrategy,
                userService,
                "test-job-id",
                userRoleRepository);

        // Inject the mocked RestTemplate
        ReflectionTestUtils.setField(studentProcessor, "restTemplate", restTemplate);
    }

    @Test
    public void testProcessStudent() throws Exception {
        // Setup test data
        Student student = new Student();
        student.setFirstName("john");
        student.setLastName("doe");
        student.setProgramId(1L);

        User mockUser = new User();
        mockUser.setUserId(1);

        // Mock behavior
        when(nameCleaningStrategy.clean("john")).thenReturn("John");
        when(nameCleaningStrategy.clean("doe")).thenReturn("Doe");

        ProgramResponse programResponse = new ProgramResponse();
        programResponse.setProgramName("Computer Science");
        when(restTemplate.getForObject(anyString(), eq(ProgramResponse.class))).thenReturn(programResponse);

        // Important: We need to mock the first saveStudent call to return a student
        // with an ID
        Student firstSavedStudent = new Student();
        firstSavedStudent.setStudentId(1L);
        firstSavedStudent.setFirstName("John");
        firstSavedStudent.setLastName("Doe");
        firstSavedStudent.setName("John Doe");
        firstSavedStudent.setProgramName("Computer Science");
        firstSavedStudent.setJobId("test-job-id");

        // And mock the second saveStudent call to return a student with both ID and
        // fullId
        Student secondSavedStudent = new Student();
        secondSavedStudent.setStudentId(1L);
        secondSavedStudent.setFirstName("John");
        secondSavedStudent.setLastName("Doe");
        secondSavedStudent.setName("John Doe");
        secondSavedStudent.setProgramName("Computer Science");
        secondSavedStudent.setJobId("test-job-id");
        secondSavedStudent.setStudentFullId("U011234"); // Set a mock student full ID

        // Mock the saveStudent method to return different values on consecutive calls
        when(userService.saveUser(any(User.class))).thenReturn(mockUser);
        when(userService.saveStudent(any(Student.class)))
                .thenReturn(firstSavedStudent)
                .thenReturn(secondSavedStudent);
        doNothing().when(userRoleRepository).assignStudentRole(anyInt());

        // Execute
        Student processedStudent = studentProcessor.process(student);

        // Verify
        assertNotNull(processedStudent);
        assertEquals("John Doe", processedStudent.getName());
        assertEquals("Computer Science", processedStudent.getProgramName());
        assertEquals("test-job-id", processedStudent.getJobId());
        assertNotNull(processedStudent.getStudentFullId());
        assertEquals("U011234", processedStudent.getStudentFullId());

        // Verify interactions
        verify(nameCleaningStrategy, times(2)).clean(anyString());
        verify(userService, times(2)).saveUser(any(User.class));
        verify(userService, times(2)).saveStudent(any(Student.class));
        verify(userRoleRepository).assignStudentRole(1);
    }

    @Test
    public void testProcessStudentWithNullProgramId() throws Exception {
        // Setup test data
        Student student = new Student();
        student.setFirstName("john");
        student.setLastName("doe");
        student.setProgramId(null); // Null program ID

        User mockUser = new User();
        mockUser.setUserId(1);

        // Mock behavior
        when(nameCleaningStrategy.clean("john")).thenReturn("John");
        when(nameCleaningStrategy.clean("doe")).thenReturn("Doe");
        when(userService.saveUser(any(User.class))).thenReturn(mockUser);
        when(userService.saveStudent(any(Student.class))).thenReturn(student);

        // Execute
        Student processedStudent = studentProcessor.process(student);

        // Verify
        assertNotNull(processedStudent);
        assertEquals("John Doe", processedStudent.getName());
        assertNull(processedStudent.getProgramName()); // Program name should remain null

        // Verify REST call was not made
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    public void testProcessStudentWithValidationError() {
        // Setup test data with invalid values to trigger validation error
        Student student = new Student();
        student.setFirstName(""); // Empty first name to trigger validation error
        student.setLastName("doe");

        // Execute and verify exception is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            studentProcessor.process(student);
        });

        // Verify exception message
        assertEquals("First Name cannot be empty", exception.getMessage());
    }
}