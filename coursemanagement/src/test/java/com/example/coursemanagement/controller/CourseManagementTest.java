package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@WebMvcTest(CourseManagementController.class) 
class CourseManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;  // Mock the service

    @MockitoBean
    private ModelMapper modelMapper;

    @InjectMocks
    private CourseManagementController courseManagementController;

    private ObjectMapper objectMapper;

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
        course1 = new Course(1, "Introduction to Computer Science", "CS101", new Date(), 
          new Date(), 100, "Open", "A foundational course that covers the basics of computer science, programming, and problem-solving techniques.");
        course2 = new Course(2, "Advanced Database Systems", "CS201", new Date(), 
          new Date(), 50, "Open", "This course explores complex database systems, including distributed databases, data warehousing, and SQL optimization.");
        course3 = new Course(3, "Cloud Computing Basics", "CS501", new Date(), 
          new Date(), 80, "Open", "An introductory course to computer science");
        course4 = new Course(4,"Course Test", "ACS101", new Date(), 
          new Date(), 100, "Open", "Test Description");
        courses = Arrays.asList(course1, course2, course3, course4);

        courseDTO1 = new CourseDTO(1, "Introduction to Computer Science", "CS101", new Date(), 
          new Date(), 100, "Open", "A foundational course that covers the basics of computer science, programming, and problem-solving techniques.");
        courseDTO2 = new CourseDTO(2, "Advanced Database Systems", "CS201", new Date(), 
          new Date(), 50, "Open", "This course explores complex database systems, including distributed databases, data warehousing, and SQL optimization.");
        courseDTO3 = new CourseDTO(3, "Cloud Computing Basics", "CS501", new Date(), 
          new Date(), 80, "Open", "An introductory course to computer science");
        courseDTO4 = new CourseDTO(4,"Course Test", "ACS101", new Date(), 
          new Date(), 100, "Open", "Test Description");

        objectMapper = new ObjectMapper();
    }

    // Test for /getcourses endpoint
    @Test
    void testGetCourses() throws Exception {
      
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
    void testGetCourse() throws Exception {

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
    void testSearchCoursesBlank() throws Exception {

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
    void testSearchCoursesWithCourseCode() throws Exception {

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
    void testSearchCoursesWithCourseName() throws Exception {

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
 
   @Test
    void addCourse() throws Exception {
        // Create a CourseDTO to be sent in the request body
        CourseDTO newCourseDTO = new CourseDTO(
            99, 
            "Test New Course", 
            "CS000", 
            new Date(), 
            new Date(), 
            30, 
            "Open", 
            "Test New Desc."
        );
        
        // Create the corresponding Course entity to be returned by the service
        Course newCourse = new Course(
            99, 
            "Test New Course", 
            "CS000", 
            new Date(), 
            new Date(), 
            30, 
            "Open", 
            "Test New Desc."
        );
        
        // Use ArgumentCaptor to capture the actual Course object passed to the service
        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        
        // Mock the behavior of the service and mapper
        when(modelMapper.map(any(CourseDTO.class), eq(Course.class))).thenReturn(newCourse);
        when(courseService.addCourse(any(Course.class))).thenReturn(newCourse);
        when(modelMapper.map(any(Course.class), eq(CourseDTO.class))).thenReturn(newCourseDTO);
        
        // Perform the POST request to add the new course
        mockMvc.perform(post("/api/courses/addCourse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.courseId").value(newCourseDTO.getCourseId()))
                .andExpect(jsonPath("$.courseName").value(newCourseDTO.getCourseName()));
        
        // Verify that the service method was called with a Course object
        verify(courseService).addCourse(courseCaptor.capture());
        
        // Additional verification if needed
        Course capturedCourse = courseCaptor.getValue();
        assertEquals(newCourseDTO.getCourseCode(), capturedCourse.getCourseCode());
    }

    @Test
    void editCourse() throws Exception {
      // Setup test data
      int courseId = 1;
      
      // Create a CourseDTO with updated information
      CourseDTO updatedCourseDTO = new CourseDTO(
          courseId, 
          "Updated Course Name", 
          "CS101", 
          new Date(), 
          new Date(), 
          40, 
          "Closed", 
          "Updated description"
      );
      
      // Create the Course entity that will be returned after updating
      Course updatedCourse = new Course(
          courseId, 
          "Updated Course Name", 
          "CS101", 
          updatedCourseDTO.getRegistrationStart(), 
          updatedCourseDTO.getRegistrationEnd(), 
          40, 
          "Closed", 
          "Updated description"
      );
      
      // Mock the behavior of the service and mapper
      when(courseService.getCourseById(courseId)).thenReturn(course1);
      when(courseService.editCourse(any(Course.class))).thenReturn(updatedCourse);
      when(modelMapper.map(any(Course.class), eq(CourseDTO.class))).thenReturn(updatedCourseDTO);
      
      // Mock the behavior for updating the existing course - important to use any() here
      doAnswer(invocation -> {
          CourseDTO source = invocation.getArgument(0);
          Course target = invocation.getArgument(1);
          
          // Update the target object with values from source
          target.setCourseName(source.getCourseName());
          target.setCourseCode(source.getCourseCode());
          target.setRegistrationStart(source.getRegistrationStart());
          target.setRegistrationEnd(source.getRegistrationEnd());
          target.setMaxCapacity(source.getMaxCapacity());
          target.setStatus(source.getStatus());
          target.setCourseDesc(source.getCourseDesc());
          
          return null;
      }).when(modelMapper).map(any(CourseDTO.class), any(Course.class));
      
      // Perform the PUT request to update the course
      mockMvc.perform(put("/api/courses/editCourse/{courseId}", courseId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updatedCourseDTO)))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.courseId").value(courseId))
              .andExpect(jsonPath("$.courseName").value("Updated Course Name"))
              .andExpect(jsonPath("$.maxCapacity").value(40))
              .andExpect(jsonPath("$.status").value("Closed"))
              .andExpect(jsonPath("$.courseDesc").value("Updated description"));
      
      // Verify that the service methods were called with correct parameters
      verify(courseService).getCourseById(courseId);
      verify(courseService).editCourse(any(Course.class)); // Use matcher instead of exact object
      
      // Use argument matchers for both parameters since the exact objects aren't known
      verify(modelMapper).map(any(CourseDTO.class), any(Course.class));
      verify(modelMapper).map(any(Course.class), eq(CourseDTO.class));
  } 
}
