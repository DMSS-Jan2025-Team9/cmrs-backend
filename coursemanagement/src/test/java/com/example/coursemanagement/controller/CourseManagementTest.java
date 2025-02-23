package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.service.CourseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@WebMvcTest(CourseManagementController.class) 
public class CourseManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;  // Mock the service

    @MockitoBean
    private ModelMapper modelMapper;

    @InjectMocks
    private CourseManagementController courseManagementController;

    private Course course1;
    private Course course2;
    private Course course3;
    private Course course4;
    private List<Course> courses;

    private CourseDTO courseDTO1;
    private CourseDTO courseDTO2;
    private CourseDTO courseDTO3;
    private CourseDTO courseDTO4;


    @BeforeEach
    void setUp() {
        course1 = new Course(1L, "Introduction to Computer Science", "CS101", new Date(), 
          new Date(), 100, "Open", "A foundational course that covers the basics of computer science, programming, and problem-solving techniques.");
        course2 = new Course(2L, "Advanced Database Systems", "CS201", new Date(), 
          new Date(), 50, "Open", "This course explores complex database systems, including distributed databases, data warehousing, and SQL optimization.");
        course3 = new Course(3L, "Cloud Computing Basics", "CS501", new Date(), 
          new Date(), 80, "Open", "An introductory course to computer science");
        course4 = new Course(4L,"Course Test", "ACS101", new Date(), 
          new Date(), 100, "Open", "Test Description");
        courses = Arrays.asList(course1, course2, course3, course4);

        courseDTO1 = new CourseDTO(1L, "Introduction to Computer Science", "CS101", new Date(), 
          new Date(), 100, "Open", "A foundational course that covers the basics of computer science, programming, and problem-solving techniques.");
        courseDTO2 = new CourseDTO(2L, "Advanced Database Systems", "CS201", new Date(), 
          new Date(), 50, "Open", "This course explores complex database systems, including distributed databases, data warehousing, and SQL optimization.");
        courseDTO3 = new CourseDTO(3L, "Cloud Computing Basics", "CS501", new Date(), 
          new Date(), 80, "Open", "An introductory course to computer science");
        courseDTO4 = new CourseDTO(4L,"Course Test", "ACS101", new Date(), 
          new Date(), 100, "Open", "Test Description");
    }

    // Test for /getcourses endpoint
    @Test
    public void testGetCourses() throws Exception {
      
        when(courseService.getAllCourses()).thenReturn(courses);
        when(modelMapper.map(course1, CourseDTO.class)).thenReturn(courseDTO1);
        when(modelMapper.map(course2, CourseDTO.class)).thenReturn(courseDTO2);
        when(modelMapper.map(course3, CourseDTO.class)).thenReturn(courseDTO3);
        when(modelMapper.map(course4, CourseDTO.class)).thenReturn(courseDTO4);

        mockMvc.perform(get("/api/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value(course1.getCourseName()))
                .andExpect(jsonPath("$[1].courseCode").value(course2.getCourseCode()))
                .andExpect(jsonPath("$[2].courseName").value(course3.getCourseName()));

       verify(courseService, times(1)).getAllCourses();
    }

    // Test for /getCourses endpoint 
    @Test
    public void testGetCourse() throws Exception {

        when(courseService.getCourse("CS501")).thenReturn(course3);
        when(modelMapper.map(course3, CourseDTO.class)).thenReturn(courseDTO3);

        mockMvc.perform(get("/api/courses/CS501")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value(course3.getCourseName()))
                .andExpect(jsonPath("$.courseCode").value(course3.getCourseCode()));

        verify(courseService, times(1)).getCourse("CS501");
    }

    @Test
    public void testSearchCoursesBlank() throws Exception {

        when(courseService.searchCourse("","")).thenReturn(courses);
        when(modelMapper.map(course1, CourseDTO.class)).thenReturn(courseDTO1);
        when(modelMapper.map(course2, CourseDTO.class)).thenReturn(courseDTO2);
        when(modelMapper.map(course3, CourseDTO.class)).thenReturn(courseDTO3);
        when(modelMapper.map(course4, CourseDTO.class)).thenReturn(courseDTO4);

        mockMvc.perform(get("/api/courses/searchCourses")
                .param("courseCode", "")
                .param("courseName", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseCode").value(course1.getCourseCode()))
                .andExpect(jsonPath("$[1].courseCode").value(course2.getCourseCode()))
                .andExpect(jsonPath("$[2].courseCode").value(course3.getCourseCode()))
                .andExpect(jsonPath("$[3].courseCode").value(course4.getCourseCode()));

       verify(courseService, times(1)).searchCourse("","");
    }

    @Test
    public void testSearchCoursesWithCourseCode() throws Exception {

        when(courseService.searchCourse("CS1", "")).thenReturn(Arrays.asList(course1, course4));
        when(modelMapper.map(course1, CourseDTO.class)).thenReturn(courseDTO1);
        when(modelMapper.map(course2, CourseDTO.class)).thenReturn(courseDTO2);
        when(modelMapper.map(course3, CourseDTO.class)).thenReturn(courseDTO3);
        when(modelMapper.map(course4, CourseDTO.class)).thenReturn(courseDTO4);

        mockMvc.perform(get("/api/courses/searchCourses")
                .param("courseCode", "CS1")
                .param("courseName", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseCode").value(course1.getCourseCode()))
                .andExpect(jsonPath("$[1].courseCode").value(course4.getCourseCode()));

       verify(courseService, times(1)).searchCourse("CS1", "");
    }

    @Test
    public void testSearchCoursesWithCourseName() throws Exception {

        when(courseService.searchCourse("","compu")).thenReturn(Arrays.asList(course1, course3));
        when(modelMapper.map(course1, CourseDTO.class)).thenReturn(courseDTO1);
        when(modelMapper.map(course2, CourseDTO.class)).thenReturn(courseDTO2);
        when(modelMapper.map(course3, CourseDTO.class)).thenReturn(courseDTO3);
        when(modelMapper.map(course4, CourseDTO.class)).thenReturn(courseDTO4);

        mockMvc.perform(get("/api/courses/searchCourses")
                .param("courseCode", "")
                .param("courseName", "compu")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value(course1.getCourseName()))
                .andExpect(jsonPath("$[1].courseName").value(course3.getCourseName()));

       verify(courseService, times(1)).searchCourse("","compu");
    }
}


