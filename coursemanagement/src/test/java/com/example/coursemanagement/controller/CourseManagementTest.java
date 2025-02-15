package com.example.coursemanagement.controller;

import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.repository.CourseRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(CourseManagementController.class) 
public class CourseManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseRepository courseRepository;


    // Test for /getcourse endpoint
    @Test
      public void testGetCourses() throws Exception {
        Course course1 = new Course();
        course1.setCourseId(1L);
        course1.setCourseName("Introduction to Computer Science");
        course1.setCourseCode("CS101");

        Course course2 = new Course();
        course2.setCourseId(2L);
        course2.setCourseName("Advanced Database Systems");
        course2.setCourseCode("CS201");

        List<Course> courses = Arrays.asList(course1, course2);

        when(courseRepository.findAll()).thenReturn(courses);

        mockMvc.perform(get("/api/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value("Introduction to Computer Science"))
                .andExpect(jsonPath("$[1].courseCode").value("CS201"));
    }

    // Test for /getCourses endpoint
    @Test
    public void testGetCourse() throws Exception {
        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Introduction to Computer Science");
        course.setCourseCode("CS101");

        List<Course> courses = Arrays.asList(course);

        when(courseRepository.findByCourseCode("CS101")).thenReturn(courses);

        mockMvc.perform(get("/api/courses/CS101")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value("Introduction to Computer Science"));
    } 
}
