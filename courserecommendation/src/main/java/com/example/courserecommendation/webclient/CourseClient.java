package com.example.courserecommendation.webclient;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.courserecommendation.dto.CourseDTO;

@Component
public class CourseClient {

    private final WebClient webClient;

    @Value("${course.service.url}")
    private String courseServiceUrl;

    public CourseClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(courseServiceUrl).build();
    }

    /**
     * Fetch all courses from the external course service.
     * 
     * @return List of courses.
     */
    public List<CourseDTO> getAllCourses() {
        return webClient.get()
                .uri("/courses") // Adjust this endpoint to your external microservice's endpoint
                .retrieve()
                .bodyToFlux(CourseDTO.class) // Mapping the response body to CourseDto
                .collectList()
                .block(); // block() will make it synchronous; in a reactive setup, you would return Flux instead
    }
}
