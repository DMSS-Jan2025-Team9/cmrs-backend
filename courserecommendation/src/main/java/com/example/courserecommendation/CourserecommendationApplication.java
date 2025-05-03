package com.example.courserecommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CourserecommendationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourserecommendationApplication.class, args);
	}

}
