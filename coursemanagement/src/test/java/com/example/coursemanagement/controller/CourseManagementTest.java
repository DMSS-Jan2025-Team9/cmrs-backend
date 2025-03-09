package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void testGetCourseByCode() throws Exception {

        when(courseService.getCourse("CS501")).thenReturn(course3);
        when(modelMapper.map(course3, CourseDTO.class)).thenReturn(courseDTO3);

        mockMvc.perform(get("/api/courses/courseCode/CS501")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value(course3.getCourseName()))
                .andExpect(jsonPath("$.courseCode").value(course3.getCourseCode()));

        verify(courseService, times(1)).getCourse("CS501");
    }

    @Test
    void testGetCourseNotFound() throws Exception {
        // Test case for course not found
        when(courseService.getCourse("INVALID")).thenReturn(null);
    
        mockMvc.perform(get("/api/courses/courseCode/INVALID")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    
        verify(courseService, times(1)).getCourse("INVALID");
    }

    @Test
    void testGetCourseById() throws Exception {
        // Test retrieving a course by ID - successful case
        int courseId = 1;
        
        when(courseService.getCourseById(courseId)).thenReturn(course1);
        when(modelMapper.map(course1, CourseDTO.class)).thenReturn(courseDTO1);

        mockMvc.perform(get("/api/courses/courseId/{courseId}", courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(course1.getCourseId()))
                .andExpect(jsonPath("$.courseName").value(course1.getCourseName()))
                .andExpect(jsonPath("$.courseCode").value(course1.getCourseCode()))
                .andExpect(jsonPath("$.maxCapacity").value(course1.getMaxCapacity()))
                .andExpect(jsonPath("$.status").value(course1.getStatus()));

        verify(courseService, times(1)).getCourseById(courseId);
        verify(modelMapper, times(1)).map(course1, CourseDTO.class);
    }

    @Test
    void testGetCourseByIdNotFound() throws Exception {
        // Test retrieving a non-existent course by ID
        int nonExistentCourseId = 999;
        
        when(courseService.getCourseById(nonExistentCourseId)).thenReturn(null);

        mockMvc.perform(get("/api/courses/courseId/{courseId}", nonExistentCourseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(courseService, times(1)).getCourseById(nonExistentCourseId);
    }

    @Test
    void testGetCourseByIdInvalidId() throws Exception {
        // Test with an invalid ID format (non-numeric)
        mockMvc.perform(get("/api/courses/courseId/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
    void testSearchCoursesWithBothParameters() throws Exception {
        // Test searching with both course code and name parameters
        when(courseService.searchCourse("CS5", "Computing")).thenReturn(Arrays.asList(course3));
        when(modelMapper.map(course3, CourseDTO.class)).thenReturn(courseDTO3);

        mockMvc.perform(get("/api/courses/searchCourses")
                .param("courseCode", "CS5")
                .param("courseName", "Computing")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseCode").value(course3.getCourseCode()))
                .andExpect(jsonPath("$[0].courseName").value(course3.getCourseName()));

        verify(courseService, times(1)).searchCourse("CS5", "Computing");
    }

    @Test
    void testSearchCoursesNoResults() throws Exception {
        // Test searching with no matching results
        when(courseService.searchCourse("XYZ", "")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/courses/searchCourses")
                .param("courseCode", "XYZ")
                .param("courseName", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(courseService, times(1)).searchCourse("XYZ", "");
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
    void testAddCourseWithDuplicateCode() throws Exception {
        // Test adding a course with an existing course code
        CourseDTO newCourseDTO = new CourseDTO(
            5, 
            "Duplicate Code Course", 
            "CS101", // Using existing code from course1
            new Date(), 
            new Date(), 
            30, 
            "Open", 
            "Test Duplicate Code"
        );
        
        Course newCourse = new Course(
            5, 
            "Duplicate Code Course", 
            "CS101", 
            new Date(), 
            new Date(), 
            30, 
            "Open", 
            "Test Duplicate Code"
        );
        
        when(modelMapper.map(any(CourseDTO.class), eq(Course.class))).thenReturn(newCourse);
        // Simulate the service throwing an exception for duplicate code
        when(courseService.addCourse(any(Course.class))).thenThrow(new DuplicateIDException("Course code already exists"));
        
        mockMvc.perform(post("/api/courses/addCourse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andDo(print()) 
                .andExpect(status().isConflict());
        
        verify(courseService).addCourse(any(Course.class));
    }

    @Test
    void testAddCourseWithInvalidCapacity() throws Exception {
        // Test adding a course with invalid capacity (negative)
        CourseDTO invalidCourseDTO = new CourseDTO(
            6, 
            "Invalid Capacity Course", 
            "CS999", 
            new Date(), 
            new Date(), 
            -10, // Invalid capacity
            "Open", 
            "Test Invalid Capacity"
        );
        
        Course invalidCourse = new Course(
            6, 
            "Invalid Capacity Course", 
            "CS999", 
            new Date(), 
            new Date(), 
            -10, 
            "Open", 
            "Test Invalid Capacity"
        );
        
        // Mock the modelMapper to return your course entity
        when(modelMapper.map(any(CourseDTO.class), eq(Course.class))).thenReturn(invalidCourse);
        
        // Mock the service to throw InvalidCapacityException
        when(courseService.addCourse(any(Course.class))).thenThrow(new InvalidCapacityException("Capacity must be a positive number"));
        
        mockMvc.perform(post("/api/courses/addCourse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCourseDTO)))
                .andExpect(status().isBadRequest());
                
        verify(courseService).addCourse(any(Course.class));
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

  @Test
  void testEditCourseNotFound() throws Exception {
      // Test editing a non-existent course
      int nonExistentCourseId = 999;
      
      CourseDTO updatedCourseDTO = new CourseDTO(
          nonExistentCourseId, 
          "Non-existent Course", 
          "CS999", 
          new Date(), 
          new Date(), 
          40, 
          "Open", 
          "This course doesn't exist"
      );
      
      when(courseService.getCourseById(nonExistentCourseId)).thenReturn(null);
      
      mockMvc.perform(put("/api/courses/editCourse/{courseId}", nonExistentCourseId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updatedCourseDTO)))
              .andExpect(status().isNotFound());
      
      verify(courseService).getCourseById(nonExistentCourseId);
      // Verify editCourse was never called
      verify(courseService, times(0)).editCourse(any(Course.class));
  }
  
  @Test
  void testEditCourseInvalidDates() throws Exception {
      // Test editing a course with invalid registration dates (end date before start date)
      int courseId = 1;
      
      // Create dates where end is before start
      Date startDate = new Date(System.currentTimeMillis() + 1000000); // Future date
      Date endDate = new Date(System.currentTimeMillis()); // Current date (before start)
      
      CourseDTO invalidDatesDTO = new CourseDTO(
          courseId,
          "Invalid Dates Course",
          "CS101",
          startDate,
          endDate, // End date before start date
          40,
          "Open",
          "Course with invalid registration dates"
      );
      
      Course existingCourse = new Course(
          courseId,
          "Original Course",
          "CS101",
          new Date(),
          new Date(),
          30,
          "Open",
          "Original description"
      );
      
      // First mock getCourseById to return an existing course
      when(courseService.getCourseById(courseId)).thenReturn(existingCourse);
      
      // Then mock editCourse to throw InvalidDateException
      when(courseService.editCourse(any(Course.class)))
          .thenThrow(new InvalidDateException("Start date cannot be after end date"));
      
      mockMvc.perform(put("/api/courses/editCourse/{courseId}", courseId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDatesDTO)))
              .andExpect(status().isBadRequest());
      
      verify(courseService).getCourseById(courseId);
      verify(courseService).editCourse(any(Course.class));
  } 

}
