package com.example.usermanagement.listener;

import com.example.usermanagement.dto.Student;
import com.example.usermanagement.repository.StudentRepository;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class StudentJobCompletionListener implements JobExecutionListener {

}
