package com.example.usermanagement.listener;


import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StepExecutionListener extends StepExecutionListenerSupport {
    // Store job status for each jobId
    private Map<String, StudentJobCompletionListener.JobStatus> jobStatusMap = new HashMap<>();

    @Autowired
    private JobStatusService jobStatusService; // Assuming a service to update job statuses

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String jobId = stepExecution.getJobExecutionId().toString();

        // Initialize job status if not already present
        if (!jobStatusMap.containsKey(jobId)) {
            StudentJobCompletionListener.JobStatus initialStatus = new StudentJobCompletionListener.JobStatus(jobId, "In Progress", "0%");
            jobStatusMap.put(jobId, initialStatus);
            jobStatusService.updateJobStatus(initialStatus); // Update status in the system or DB
        }
        return null; // void method, so no return is required here
    }

    @Override
    public void afterStep(StepExecution stepExecution) {
        String jobId = stepExecution.getJobExecutionId().toString();
        StudentJobCompletionListener.JobStatus currentStatus = jobStatusMap.get(jobId);

        // Update progress based on steps processed
        if (stepExecution.getCommitCount() > 0) {
            double progress = (stepExecution.getReadCount() / (double) stepExecution.getCommitCount()) * 100;
            currentStatus.setProgress(String.format("%.2f%%", progress));
        }

        // Check if the step has completed and update status accordingly
        if (stepExecution.getReadCount() == stepExecution.getWriteCount()) {
            currentStatus.setStatus("Completed");
        }

        // Update job status in the system or database
        jobStatusService.updateJobStatus(currentStatus);

        // No return needed as it's a void method
    }

    // Getter for job status map (if needed)
    public Map<String, StudentJobCompletionListener.JobStatus> getJobStatusMap() {
        return jobStatusMap;
    }
}
