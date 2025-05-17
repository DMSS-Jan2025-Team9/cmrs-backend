package com.example.usermanagement.controller;

import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private JobController jobController;

    private MockMultipartFile mockFile;
    private Student student1;
    private Student student2;

    @BeforeEach
    public void setUp() {
        // Set up mock CSV file
        String csvContent = "firstName,lastName,matriculationNumber,program\nJohn,Doe,A123456,Computer Science\nJane,Smith,A789012,Data Science";
        mockFile = new MockMultipartFile(
                "csvFile",
                "students.csv",
                "text/csv",
                csvContent.getBytes());

        // Set up test students
        student1 = new Student();
        student1.setStudentId(1L);
        student1.setName("John Doe");
        student1.setStudentFullId("A123456");
        student1.setProgramName("Computer Science");

        student2 = new Student();
        student2.setStudentId(2L);
        student2.setName("Jane Smith");
        student2.setStudentFullId("A789012");
        student2.setProgramName("Data Science");
    }

    @Test
    public void testImportCsvToDBJob() throws Exception {
        // Setup mocks
        when(studentRepository.findByJobId(anyString())).thenReturn(Arrays.asList(student1, student2));

        // Execute
        ResponseEntity<Object> response = jobController.importCsvToDBJob(mockFile);

        // Verify the job was launched
        verify(jobLauncher, times(1)).run(eq(job), any(JobParameters.class));

        // Verify students were fetched with the job ID
        verify(studentRepository, times(1)).findByJobId(anyString());

        // Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<?> responseList = (List<?>) response.getBody();
        assertEquals(2, responseList.size());
        assertTrue(responseList.get(0) instanceof StudentDto);
    }

    @Test
    public void testJobParametersBuiltCorrectly() throws Exception {
        // Setup to capture job parameters
        ArgumentCaptor<JobParameters> paramsCaptor = ArgumentCaptor.forClass(JobParameters.class);

        // Execute
        jobController.importCsvToDBJob(mockFile);

        // Verify and capture parameters
        verify(jobLauncher).run(eq(job), paramsCaptor.capture());

        // Verify parameters
        JobParameters capturedParams = paramsCaptor.getValue();
        assertNotNull(capturedParams.getString("csvFile"));
        assertNotNull(capturedParams.getString("jobId"));
        assertNotNull(capturedParams.getLong("startAt"));

        // Verify file path points to a real file (in temp directory)
        String filePath = capturedParams.getString("csvFile");
        assertTrue(filePath.contains("students.csv"));
    }
}