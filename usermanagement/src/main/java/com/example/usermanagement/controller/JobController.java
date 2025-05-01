package com.example.usermanagement.controller;

import com.example.usermanagement.dto.StudentDto;
import com.example.usermanagement.mapper.StudentMapper;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobLauncher jobLauncher;

    private final Job job;
    private final StudentRepository studentRepository;
    private final UserService userService;

    // Constructor Injection (Best Practice)
    public JobController(JobLauncher jobLauncher, Job job, UserService userService,
            StudentRepository studentRepository) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.userService = userService;
        this.studentRepository = studentRepository;
    }

    // @PostMapping("/importStudents")
    // public void importCsvToDBJob() {
    // JobParameters jobParameters = new JobParametersBuilder()
    // .addLong("startAt", System.currentTimeMillis()).toJobParameters();
    // try {
    // jobLauncher.run(job, jobParameters);
    // } catch (JobExecutionAlreadyRunningException | JobRestartException |
    // JobInstanceAlreadyCompleteException |
    // JobParametersInvalidException e) {
    // e.printStackTrace();
    // }
    // }

    @PostMapping("/importStudents")
    @Operation(summary = "Upload CSV and trigger batch job", description = "Uploads a CSV file to be processed by the batch job.")
    public ResponseEntity<Object> importCsvToDBJob(
            @RequestParam("csvFile") MultipartFile file) throws IOException {

        // Use a relative path that will work in any environment
        String baseDirectory = System.getProperty("java.io.tmpdir");
        String relativePath = "cmrs-students-list";
        String directoryPath = new File(baseDirectory, relativePath).getAbsolutePath();

        // Create a File object for the directory
        File directory = new File(directoryPath);

        // Check if directory exists, create it if it doesn't
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to create directory for file upload");
            }
        }

        // Save the file temporarily to disk
        File tempFile = new File(directory, file.getOriginalFilename());
        file.transferTo(tempFile);

        String jobId = "job_" + System.currentTimeMillis(); // Unique job ID

        // Trigger the job
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("csvFile", tempFile.getAbsolutePath())
                .addString("jobId", jobId) // Pass job ID to Job
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to launch job: " + e.getMessage());
        }

        List<Student> students = studentRepository.findByJobId(jobId);

        List<StudentDto> studentDtos = students.stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentDtos);
    }

}
