package com.example.courserecommendation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(CourseRecommendationController.class) 
public class CourseRecommendationTest {

    @Autowired
    private MockMvc mockMvc;

    // Test for /getCourses endpoint
    @Test
    public void testGetCourses() throws Exception {

        mockMvc.perform(get("/api/courseRecommendation/getCourses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Getting list of courses to recommend"));
    }
}
