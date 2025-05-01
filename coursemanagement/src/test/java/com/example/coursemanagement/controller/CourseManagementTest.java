package com.example.coursemanagement.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
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

    private Integer programId1 = 1;
    private Integer programId2 = 2;

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
          new Date(), 100, "Open", "A foundational course that covers the basics of computer science, programming, and problem-solving techniques.", programId1);
        courseDTO2 = new CourseDTO(2, "Advanced Database Systems", "CS201", new Date(), 
          new Date(), 50, "Open", "This course explores complex database systems, including distributed databases, data warehousing, and SQL optimization.", programId1);
        courseDTO3 = new CourseDTO(3, "Cloud Computing Basics", "CS501", new Date(), 
          new Date(), 80, "Open", "An introductory course to computer science", programId2);
        courseDTO4 = new CourseDTO(4,"Course Test", "ACS101", new Date(), 
          new Date(), 100, "Open", "Test Description", programId2);

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

    @Test
    void testGetAllActiveCourses() throws Exception {
        // Create a list of active courses with appropriate registration dates
        Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24); // Yesterday
        Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7); // One week from now

        Course activeCourse1 = new Course(1, "Active Course 1", "AC101", past, future, 100, "active", "Description 1");
        Course activeCourse2 = new Course(2, "Active Course 2", "AC102", past, future, 50, "active", "Description 2");
        Course activeCourse3 = new Course(3, "Active Course 3", "AC103", past, future, 80, "active", "Description 3");
        
        List<Course> activeCourses = Arrays.asList(activeCourse1, activeCourse2, activeCourse3);
        
        // Create corresponding DTOs
        CourseDTO activeCourseDTO1 = new CourseDTO(1, "Active Course 1", "AC101", past, future, 100, "active", "Description 1", programId1);
        CourseDTO activeCourseDTO2 = new CourseDTO(2, "Active Course 2", "AC102", past, future, 50, "active", "Description 2", programId1);
        CourseDTO activeCourseDTO3 = new CourseDTO(3, "Active Course 3", "AC103", past, future, 80, "active", "Description 3", programId2);
        
        // Mock the service method to return the active courses
        when(courseService.findAllActiveCourses()).thenReturn(activeCourses);
        
        // Mock the model mapper for each course
        when(modelMapper.map(activeCourse1, CourseDTO.class)).thenReturn(activeCourseDTO1);
        when(modelMapper.map(activeCourse2, CourseDTO.class)).thenReturn(activeCourseDTO2);
        when(modelMapper.map(activeCourse3, CourseDTO.class)).thenReturn(activeCourseDTO3);

        // Perform the GET request
        mockMvc.perform(get("/api/courses/getActiveCourses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].courseId").value(activeCourse1.getCourseId()))
                .andExpect(jsonPath("$[0].status").value("active"))
                .andExpect(jsonPath("$[1].courseId").value(activeCourse2.getCourseId()))
                .andExpect(jsonPath("$[2].courseId").value(activeCourse3.getCourseId()));

        // Verify the service method was called once
        verify(courseService, times(1)).findAllActiveCourses();
    }

    @Test
    void testGetAllActiveCoursesEmptyList() throws Exception {
        // Mock the service method to return an empty list (no active courses)
        when(courseService.findAllActiveCourses()).thenReturn(Collections.emptyList());
        
        // Perform the GET request
        mockMvc.perform(get("/api/courses/getActiveCourses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        // Verify the service method was called once
        verify(courseService, times(1)).findAllActiveCourses();
    }

    @Test
    void testGetAllActiveCoursesMixedStatus() throws Exception {
        // Create courses with different status values
        Course activeCourse1 = new Course(1, "Active Course 1", "AC101", new Date(), 
                                        new Date(), 100, "Open", "Description 1");
        Course activeCourse2 = new Course(2, "Active Course 2", "AC102", new Date(), 
                                        new Date(), 50, "Open", "Description 2");
        Course inactiveCourse = new Course(3, "Inactive Course", "IC101", new Date(), 
                                        new Date(), 80, "Closed", "Description 3");
        
        // Create corresponding DTOs
        CourseDTO activeCourseDTO1 = new CourseDTO(1, "Active Course 1", "AC101", new Date(), 
                                                new Date(), 100, "Open", "Description 1", programId1);
        CourseDTO activeCourseDTO2 = new CourseDTO(2, "Active Course 2", "AC102", new Date(), 
                                                new Date(), 50, "Open", "Description 2", programId1);
        
        // Create a list of active courses
        List<Course> activeCourses = Arrays.asList(activeCourse1, activeCourse2);
        
        // Mock the service method to return only active courses
        when(courseService.findAllActiveCourses()).thenReturn(activeCourses);
        
        // Mock the model mapper for each active course
        when(modelMapper.map(activeCourse1, CourseDTO.class)).thenReturn(activeCourseDTO1);
        when(modelMapper.map(activeCourse2, CourseDTO.class)).thenReturn(activeCourseDTO2);

        // Perform the GET request
        mockMvc.perform(get("/api/courses/getActiveCourses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].courseId").value(activeCourse1.getCourseId()))
                .andExpect(jsonPath("$[0].status").value("Open"))
                .andExpect(jsonPath("$[1].courseId").value(activeCourse2.getCourseId()))
                .andExpect(jsonPath("$[1].status").value("Open"));

        // Verify the service method was called once
        verify(courseService, times(1)).findAllActiveCourses();
    }

    @Test
    void testGetAllActiveCoursesWithDateFiltering() throws Exception {
        // Current time reference
        Date now = new Date();
        
        // Valid date ranges (registration open)
        Date pastStart = new Date(now.getTime() - 1000 * 60 * 60 * 24 * 2); // 2 days ago
        Date futureEnd = new Date(now.getTime() + 1000 * 60 * 60 * 24 * 5);  // 5 days from now
        
        // Invalid date ranges
        Date futureStart = new Date(now.getTime() + 1000 * 60 * 60 * 24);    // Tomorrow (not started yet)
        Date pastEnd = new Date(now.getTime() - 1000 * 60 * 60 * 24);        // Yesterday (already ended)
        
        // Create courses with different statuses and date combinations
        Course validActiveCourse = new Course(1, "Valid Active Course", "VAC101", 
                pastStart, futureEnd, 100, "active", "Registration is open");
        
        Course notStartedCourse = new Course(2, "Not Started Course", "NSC101", 
                futureStart, futureEnd, 50, "active", "Registration not started yet");
        
        Course endedCourse = new Course(3, "Ended Course", "EC101", 
                pastStart, pastEnd, 80, "active", "Registration already ended");
        
        Course inactiveCourse = new Course(4, "Inactive Course", "IC101", 
                pastStart, futureEnd, 70, "inactive", "Course is inactive");
        
        // Create DTOs for the valid course since only it should be returned
        CourseDTO validActiveCourseDTO = new CourseDTO(1, "Valid Active Course", "VAC101", 
                pastStart, futureEnd, 100, "active", "Registration is open", programId1);
        
        // We only expect the valid course to be returned by the service
        List<Course> filteredCourses = Arrays.asList(validActiveCourse);
        
        // Mock the service method to return only the valid active course
        when(courseService.findAllActiveCourses()).thenReturn(filteredCourses);
        
        // Mock the model mapper for the valid course
        when(modelMapper.map(validActiveCourse, CourseDTO.class)).thenReturn(validActiveCourseDTO);

        // Perform the GET request
        mockMvc.perform(get("/api/courses/getActiveCourses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].courseId").value(validActiveCourse.getCourseId()))
                .andExpect(jsonPath("$[0].status").value("active"))
                .andExpect(jsonPath("$[0].courseName").value("Valid Active Course"));

        // Verify the service method was called once
        verify(courseService, times(1)).findAllActiveCourses();
        
        // The service implementation should handle the date filtering, not the controller
        // So these courses should never reach the controller
        verify(modelMapper, never()).map(notStartedCourse, CourseDTO.class);
        verify(modelMapper, never()).map(endedCourse, CourseDTO.class);
        verify(modelMapper, never()).map(inactiveCourse, CourseDTO.class);
    }


    @Test
    void testGetAllActiveCoursesWithMixedStatusesAndDates() throws Exception {
        // Setup dates
        Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24); // Yesterday
        Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7); // One week from now
        Date veryPast = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 30); // 30 days ago
        Date veryFuture = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30); // 30 days from now
        
        // Create a variety of courses with different statuses and date combinations
        Course activeValidCourse1 = new Course(1, "Active Valid 1", "AV101", past, future, 100, "active", "Active and valid dates");
        Course activeValidCourse2 = new Course(2, "Active Valid 2", "AV102", veryPast, veryFuture, 50, "active", "Active with wider date range");
        
        Course activeInvalidDates = new Course(3, "Active Invalid Dates", "AID101", future, veryFuture, 80, "active", "Active but registration not started");
        Course inactiveValidDates = new Course(4, "Inactive Valid Dates", "IVD101", past, future, 70, "inactive", "Inactive with valid dates");
        
        // Setup DTOs for valid courses that should be returned
        CourseDTO activeValidCourseDTO1 = new CourseDTO(1, "Active Valid 1", "AV101", past, future, 100, "active", "Active and valid dates", programId1);
        CourseDTO activeValidCourseDTO2 = new CourseDTO(2, "Active Valid 2", "AV102", veryPast, veryFuture, 50, "active", "Active with wider date range", programId2);
        
        // Only the active courses with valid registration dates should be returned
        List<Course> validCourses = Arrays.asList(activeValidCourse1, activeValidCourse2);
        
        // Mock service to return only valid courses
        when(courseService.findAllActiveCourses()).thenReturn(validCourses);
        
        // Mock mapper for valid courses
        when(modelMapper.map(activeValidCourse1, CourseDTO.class)).thenReturn(activeValidCourseDTO1);
        when(modelMapper.map(activeValidCourse2, CourseDTO.class)).thenReturn(activeValidCourseDTO2);
        
        // Perform request
        mockMvc.perform(get("/api/courses/getActiveCourses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].courseId").value(activeValidCourse1.getCourseId()))
                .andExpect(jsonPath("$[0].status").value("active"))
                .andExpect(jsonPath("$[1].courseId").value(activeValidCourse2.getCourseId()))
                .andExpect(jsonPath("$[1].status").value("active"));
        
        // Verify service was called
        verify(courseService, times(1)).findAllActiveCourses();
        
        // These should not be mapped since they should be filtered out by the service
        verify(modelMapper, never()).map(activeInvalidDates, CourseDTO.class);
        verify(modelMapper, never()).map(inactiveValidDates, CourseDTO.class);
    }

    @Test
    void testGetAllActiveCoursesWithDateEdgeCases() throws Exception {
        // Current date/time reference
        Date now = new Date();
        
        // Edge case: registration starts exactly now
        Date startNow = now;
        
        // Edge case: registration ends exactly now
        Date endNow = now;
        
        // Edge case: registration started exactly at midnight today
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startToday = cal.getTime();
        
        // Create courses with edge case dates
        Course startNowCourse = new Course(1, "Start Now Course", "SN101", 
                startNow, new Date(now.getTime() + 86400000), 100, "active", "Registration starts now");
        
        Course endNowCourse = new Course(2, "End Now Course", "EN101", 
                new Date(now.getTime() - 86400000), endNow, 50, "active", "Registration ends now");
        
        Course startTodayCourse = new Course(3, "Start Today Course", "ST101", 
                startToday, new Date(now.getTime() + 86400000 * 7), 80, "active", "Registration started today at midnight");
        
        // The service implementation should determine which courses are valid according to business rules
        // For this test, we'll assume the service includes startNowCourse and startTodayCourse
        // but excludes endNowCourse (since registration is ending exactly now)
        List<Course> validCourses = Arrays.asList(startNowCourse, startTodayCourse);
        
        // Create corresponding DTOs
        CourseDTO startNowCourseDTO = new CourseDTO(1, "Start Now Course", "SN101", 
                startNow, new Date(now.getTime() + 86400000), 100, "active", "Registration starts now", programId1);
        CourseDTO startTodayCourseDTO = new CourseDTO(3, "Start Today Course", "ST101", 
                startToday, new Date(now.getTime() + 86400000 * 7), 80, "active", "Registration started today at midnight", programId2);
        
        // Mock service behavior
        when(courseService.findAllActiveCourses()).thenReturn(validCourses);
        
        // Mock mapper behavior
        when(modelMapper.map(startNowCourse, CourseDTO.class)).thenReturn(startNowCourseDTO);
        when(modelMapper.map(startTodayCourse, CourseDTO.class)).thenReturn(startTodayCourseDTO);
        
        // Perform request
        mockMvc.perform(get("/api/courses/getActiveCourses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].courseId").value(startNowCourse.getCourseId()))
                .andExpect(jsonPath("$[1].courseId").value(startTodayCourse.getCourseId()));
        
        // Verify service call
        verify(courseService, times(1)).findAllActiveCourses();
    }

    // Test for /getCourses endpoint 
    @Test
    void testGetCourseByCode() throws Exception {
        String courseCode = "CS501";
        
        when(courseService.getCourseWithProgram(courseCode)).thenReturn(course3);
        when(courseService.getProgramIdForCourse(course3.getCourseId())).thenReturn(programId2);
        when(modelMapper.map(course3, CourseDTO.class)).thenReturn(courseDTO3);

        mockMvc.perform(get("/api/courses/courseCode/{courseCode}", courseCode)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value(course3.getCourseName()))
                .andExpect(jsonPath("$.courseCode").value(course3.getCourseCode()))
                .andExpect(jsonPath("$.programId").value(programId2));

        verify(courseService, times(1)).getCourseWithProgram(courseCode);
        verify(courseService, times(1)).getProgramIdForCourse(course3.getCourseId());
    }

    @Test
    void testGetCourseNotFound() throws Exception {
        // Test case for course not found
        String invalidCode = "INVALID";
        
        when(courseService.getCourseWithProgram(invalidCode))
            .thenThrow(new ResourceNotFoundException("course", "courseCode", invalidCode));
    
        mockMvc.perform(get("/api/courses/courseCode/{courseCode}", invalidCode)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    
        verify(courseService, times(1)).getCourseWithProgram(invalidCode);
    }

    @Test
    void testGetCourseById() throws Exception {
        // Test retrieving a course by ID - successful case
        int courseId = 1;
        
        when(courseService.getCourseByIdWithProgram(courseId)).thenReturn(course1);
        when(courseService.getProgramIdForCourse(courseId)).thenReturn(programId1);
        when(modelMapper.map(course1, CourseDTO.class)).thenReturn(courseDTO1);

        mockMvc.perform(get("/api/courses/courseId/{courseId}", courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(course1.getCourseId()))
                .andExpect(jsonPath("$.courseName").value(course1.getCourseName()))
                .andExpect(jsonPath("$.courseCode").value(course1.getCourseCode()))
                .andExpect(jsonPath("$.maxCapacity").value(course1.getMaxCapacity()))
                .andExpect(jsonPath("$.status").value(course1.getStatus()))
                .andExpect(jsonPath("$.programId").value(programId1));

        verify(courseService, times(1)).getCourseByIdWithProgram(courseId);
        verify(courseService, times(1)).getProgramIdForCourse(courseId);
        verify(modelMapper, times(1)).map(course1, CourseDTO.class);
    }

    @Test
    void testGetCourseByIdNotFound() throws Exception {
        // Test retrieving a non-existent course by ID
        int nonExistentCourseId = 999;
        
        when(courseService.getCourseByIdWithProgram(nonExistentCourseId))
            .thenThrow(new ResourceNotFoundException("course", "courseId", String.valueOf(nonExistentCourseId)));

        mockMvc.perform(get("/api/courses/courseId/{courseId}", nonExistentCourseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(courseService, times(1)).getCourseByIdWithProgram(nonExistentCourseId);
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
    @WithMockUser(roles = "admin")
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
            "Test New Desc.",
            programId1            
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
        ArgumentCaptor<Integer> programIdCaptor = ArgumentCaptor.forClass(Integer.class);
        
        // Mock the behavior of the service and mapper
        when(modelMapper.map(any(CourseDTO.class), eq(Course.class))).thenReturn(newCourse);
        when(courseService.addCourse(any(Course.class), any(Integer.class))).thenReturn(newCourse);
        when(modelMapper.map(any(Course.class), eq(CourseDTO.class))).thenReturn(newCourseDTO);
        
        // Perform the POST request to add the new course
        mockMvc.perform(post("/api/courses/addCourse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.courseId").value(newCourseDTO.getCourseId()))
                .andExpect(jsonPath("$.courseName").value(newCourseDTO.getCourseName()))
                .andExpect(jsonPath("$.programId").value(newCourseDTO.getProgramId()));
        
        // Verify that the service method was called with a Course object
        verify(courseService).addCourse(courseCaptor.capture(), programIdCaptor.capture());
        
        // Additional verification if needed
        Course capturedCourse = courseCaptor.getValue();
        Integer capturedProgramId = programIdCaptor.getValue();
        
        assertEquals(newCourseDTO.getCourseCode(), capturedCourse.getCourseCode());
        assertEquals(newCourseDTO.getProgramId(), capturedProgramId);
    }

    @Test
    @WithMockUser(roles = "admin")
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
            "Test Duplicate Code",
            programId1 
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
        when(courseService.addCourse(any(Course.class), eq(programId1))).thenThrow(new DuplicateIDException("Course code already exists"));
        
        mockMvc.perform(post("/api/courses/addCourse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andDo(print()) 
                .andExpect(status().isConflict());
        
        verify(courseService).addCourse(any(Course.class), eq(programId1));
    }

    @Test
    @WithMockUser(roles = "admin")
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
            "Test Invalid Capacity",
            programId1
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
        when(courseService.addCourse(any(Course.class), eq(programId1))).thenThrow(new InvalidCapacityException("Capacity must be a positive number"));
        
        mockMvc.perform(post("/api/courses/addCourse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCourseDTO)))
                .andExpect(status().isBadRequest());
                
        verify(courseService).addCourse(any(Course.class), eq(programId1));
    }

    @Test
    @WithMockUser(roles = "admin")
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
          "inactive", 
          "Updated description",
          programId2 // Changed program ID
      );
      
      // Create the Course entity that will be returned after updating
      Course updatedCourse = new Course(
          courseId, 
          "Updated Course Name", 
          "CS101", 
          updatedCourseDTO.getRegistrationStart(), 
          updatedCourseDTO.getRegistrationEnd(), 
          40, 
          "inactive", 
          "Updated description"
      );
      
      // Mock the behavior of the service and mapper
      when(courseService.getCourseById(courseId)).thenReturn(course1);
      when(courseService.editCourseWithProgram(any(Course.class), eq(programId2))).thenReturn(updatedCourse);
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
              .andExpect(jsonPath("$.status").value("inactive"))
              .andExpect(jsonPath("$.courseDesc").value("Updated description"))
              .andExpect(jsonPath("$.programId").value(programId2)); // Check program ID
      
      // Verify that the service methods were called with correct parameters
      verify(courseService).getCourseById(courseId);
      verify(courseService).editCourseWithProgram(any(Course.class), eq(programId2)); // Verify with programId
      
      // Use argument matchers for both parameters since the exact objects aren't known
      verify(modelMapper).map(any(CourseDTO.class), any(Course.class));
      verify(modelMapper).map(any(Course.class), eq(CourseDTO.class));
    } 

    @Test
    @WithMockUser(roles = "admin")
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
          "This course doesn't exist",
          programId1
      );
      
      when(courseService.getCourseById(nonExistentCourseId)).thenReturn(null);
      
      mockMvc.perform(put("/api/courses/editCourse/{courseId}", nonExistentCourseId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updatedCourseDTO)))
              .andExpect(status().isNotFound());
      
      verify(courseService).getCourseById(nonExistentCourseId);
      // Verify editCourse was never called
      verify(courseService, times(0)).editCourseWithProgram(any(Course.class), any(Integer.class));
    }
  
    @Test
    @WithMockUser(roles = "admin")
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
          "Course with invalid registration dates",
          programId1
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
      when(courseService.editCourseWithProgram(any(Course.class), eq(programId1)))
          .thenThrow(new InvalidDateException("Start date cannot be after end date"));
      
      mockMvc.perform(put("/api/courses/editCourse/{courseId}", courseId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDatesDTO)))
              .andExpect(status().isBadRequest());
      
      verify(courseService).getCourseById(courseId);
      verify(courseService).editCourseWithProgram(any(Course.class), eq(programId1));
    }
    
    @Test
    @WithMockUser(roles = "admin")
    void testEditCourseWithProgramNotFound() throws Exception {
      // Test editing a course with a non-existent program
      int courseId = 1;
      Integer nonExistentProgramId = 999;
      
      CourseDTO updatedCourseDTO = new CourseDTO(
          courseId,
          "Course With Invalid Program",
          "CS101",
          new Date(),
          new Date(),
          40,
          "Open",
          "Course with non-existent program",
          nonExistentProgramId
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
      
      // Mock getCourseById to return an existing course
      when(courseService.getCourseById(courseId)).thenReturn(existingCourse);
      
      // Mock editCourseWithProgram to throw ResourceNotFoundException for program
      when(courseService.editCourseWithProgram(any(Course.class), eq(nonExistentProgramId)))
          .thenThrow(new ResourceNotFoundException("program", "programId", nonExistentProgramId.toString()));
      
      mockMvc.perform(put("/api/courses/editCourse/{courseId}", courseId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updatedCourseDTO)))
              .andExpect(status().isNotFound());
      
      verify(courseService).getCourseById(courseId);
      verify(courseService).editCourseWithProgram(any(Course.class), eq(nonExistentProgramId));
    }
    
    @Test
    void testGetProgramIdForCourseNotFound() throws Exception {
      // Test getting program ID for a course without any program association
      int courseId = 5; // Course without program association
      
      when(courseService.getCourseByIdWithProgram(courseId)).thenReturn(new Course(
          courseId,
          "Orphan Course",
          "CS999",
          new Date(),
          new Date(),
          30,
          "Open",
          "Course without program"
      ));
      
      when(courseService.getProgramIdForCourse(courseId))
          .thenThrow(new ResourceNotFoundException("course", "courseId with program association", String.valueOf(courseId)));
      
      mockMvc.perform(get("/api/courses/courseId/{courseId}", courseId)
              .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isNotFound());
      
      verify(courseService).getCourseByIdWithProgram(courseId);
      verify(courseService).getProgramIdForCourse(courseId);
    }

    @Test
    @WithMockUser(roles = "admin")
    void deleteCourse() throws Exception {
        // Mock service to return true (successful deletion)
        when(courseService.getCourseById(1)).thenReturn(course1);
        doNothing().when(courseService).deleteCourse(1);
        
        // Perform the request
        mockMvc.perform(delete("/api/courses/deleteCourse/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Verify service was called
        verify(courseService).getCourseById(1);
        verify(courseService).deleteCourse(1);
    }

    @Test
    @WithMockUser(roles = "admin")
    void deleteCourseNotFound() throws Exception {
        // Mock service to return null (course not found)
        when(courseService.getCourseById(999)).thenReturn(null);
        
        // Perform the request
        mockMvc.perform(delete("/api/courses/deleteCourse/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Course with id 999 not found"));
        
        // Verify getCourseById was called but deleteCourse was not
        verify(courseService).getCourseById(999);
        verify(courseService, never()).deleteCourse(999);
    }

    @Test
    @WithMockUser(roles = "admin")
    void deleteCourseInvalidIdFormat() throws Exception {
        // Test with non-numeric ID
        mockMvc.perform(delete("/api/courses/deleteCourse/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        // Verify service was not called
        verify(courseService, never()).getCourseById(any(Integer.class));
        verify(courseService, never()).deleteCourse(any(Integer.class));
    }

    @Test
    @WithMockUser(roles = "admin")
    void deleteCourseResourceNotFoundException() throws Exception {
        // Mock getCourseById to return a course but deleteCourse to throw ResourceNotFoundException
        when(courseService.getCourseById(1)).thenReturn(course1);
        doThrow(new ResourceNotFoundException("Course with id 1 not found"))
            .when(courseService).deleteCourse(1);
        
        // Perform the request
        mockMvc.perform(delete("/api/courses/deleteCourse/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"));
        
        // Verify both methods were called
        verify(courseService).getCourseById(1);
        verify(courseService).deleteCourse(1);
    }
}