package com.example.coursemanagement.controller;

import com.example.coursemanagement.model.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(CourseManagementController.class) 
public class CourseManagementTest {

    @Autowired
    private MockMvc mockMvc;

    // Test for /getcourse endpoint
    @Test
    public void testGetCourse() throws Exception {
        Course course = new Course("course 1", "course 1 details");

        mockMvc.perform(get("/api/courses/getCourse")
                .param("courseName", course.getCourseName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Getting course details for: " + course.getCourseName()));
    }

    // Test for /getCourses endpoint
    @Test
    public void testGetCourses() throws Exception {

        mockMvc.perform(get("/api/courses/getCourses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Getting list of courses"));
    }
}
