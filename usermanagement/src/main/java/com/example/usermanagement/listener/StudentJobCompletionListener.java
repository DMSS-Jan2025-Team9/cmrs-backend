package com.example.usermanagement.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class StudentJobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Job started at: " + jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Job finished at: " + jobExecution.getEndTime());
        System.out.println("Job status: " + jobExecution.getStatus());
    }
}
