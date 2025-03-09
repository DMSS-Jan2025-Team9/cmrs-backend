package com.example.usermanagement.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudentStepTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job runJob; // Job bean configured in JobConfig

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary CSV file for testing
        tempFile = File.createTempFile("student_enrollment_test", ".csv");
        tempFile.deleteOnExit(); // Ensure the file is deleted when the JVM exits

        // Write sample data to the CSV file
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.append("programId,name,enrolledAt\n");
            writer.append("1,John Doe,2025-03-01\n");
            writer.append("2,Jane Smith,2025-03-02\n");
        }
    }

    @Test
    void testStepExecution() throws Exception {
        // Pass the path of the temporary CSV file as a job parameter
        JobExecution jobExecution = jobLauncher.run(runJob,
                new JobParametersBuilder()
                        .addString("csvFile", tempFile.getAbsolutePath()) // Set the file path as a parameter
                        .toJobParameters());

        // Assert job completion status
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
}
