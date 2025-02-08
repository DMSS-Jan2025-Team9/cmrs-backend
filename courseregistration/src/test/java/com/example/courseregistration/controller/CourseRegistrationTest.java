package com.example.courseregistration.controller;

import com.example.courseregistration.model.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(CourseRegistrationController.class) 
public class CourseRegistrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // Test for /getcourse endpoint
    @Test
    public void testGetCourse() throws Exception {
        Course course = new Course("course 1", "course 1 details");
        String courseJson = objectMapper.writeValueAsString(course);

        mockMvc.perform(post("/api/courseRegistration/registerCourse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Registering for course: " + course.getCourseName()));
    }

}
