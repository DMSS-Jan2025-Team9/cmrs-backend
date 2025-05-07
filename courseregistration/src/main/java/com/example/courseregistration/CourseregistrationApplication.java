package com.example.courseregistration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CourseregistrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseregistrationApplication.class, args);
	}

}
