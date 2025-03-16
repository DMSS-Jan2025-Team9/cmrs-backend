package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.ClassScheduleDTO;
import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.service.ClassScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassManagementController.class)
class ClassManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClassScheduleService classScheduleService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Course course1;
    private Course course2;
    private ClassSchedule classSchedule1;
    private ClassSchedule classSchedule2;
    private ClassSchedule classSchedule3;
    private ClassScheduleDTO classScheduleDTO1;
    private ClassScheduleDTO classScheduleDTO2;
    private ClassScheduleDTO classScheduleDTO3;
    private List<ClassSchedule> classScheduleList;
    private List<ClassScheduleDTO> classScheduleDTOList;

    @BeforeEach
    void setUp() {
        // Setup test data
        course1 = new Course(1, "Test Course", "CS101", null, null, 100, "Open", "Test Description");
        course2 = new Course(2, "Test Course 2", "CS102", null, null, 100, "Open", "Test Description");
        
        classSchedule1 = new ClassSchedule(
            1, 
            course1, 
            "Monday", 
            LocalTime.of(9, 0), 
            LocalTime.of(11, 0), 
            30, 
            30
        );
        classSchedule2 = new ClassSchedule(
            2, 
            course1, 
            "Tuesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            20, 
            20
        );
        classSchedule3 = new ClassSchedule(
            3, 
            course2, 
            "Friday", 
            LocalTime.of(15, 0), 
            LocalTime.of(17, 0), 
            40, 
            40
        );

        classScheduleDTO1 = new ClassScheduleDTO(
            1, 
            1, 
            "Test Course", 
            "CS101", 
            "Monday", 
            LocalTime.of(9, 0), 
            LocalTime.of(11, 0), 
            30, 
            30
        );
        
        classScheduleDTO2 = new ClassScheduleDTO(
            2, 
            1, 
            "Test Course", 
            "CS101", 
            "Tuesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            20, 
            20
        );
              
        classScheduleDTO3 = new ClassScheduleDTO(
            3, 
            2, 
            "Test Course 2", 
            "CS102", 
            "Friday", 
            LocalTime.of(15, 0), 
            LocalTime.of(17, 0), 
            40, 
            40
        );
        
        // Create list of class schedules
        classScheduleList = new ArrayList<>();
        classScheduleList.add(classSchedule1);
        classScheduleList.add(classSchedule2);
        classScheduleList.add(classSchedule3);
        
        classScheduleDTOList = new ArrayList<>();
        classScheduleDTOList.add(classScheduleDTO1);
        classScheduleDTOList.add(classScheduleDTO2);
        classScheduleDTOList.add(classScheduleDTO3);
    }

    @Test
    void getAllClassSchedulesForCourse() throws Exception {
        // Create a list with only the class schedules for course 1
        List<ClassSchedule> course1ClassSchedules = new ArrayList<>();
        course1ClassSchedules.add(classSchedule1);
        course1ClassSchedules.add(classSchedule2);
        
        // Mock the service to return only these class schedules
        when(classScheduleService.getAllClassSchedulesForCourse(1)).thenReturn(course1ClassSchedules);
        
        // Mock the mapper to return the corresponding DTOs
        when(modelMapper.map(classSchedule1, ClassScheduleDTO.class)).thenReturn(classScheduleDTO1);
        when(modelMapper.map(classSchedule2, ClassScheduleDTO.class)).thenReturn(classScheduleDTO2);
        
        // Perform the request
        mockMvc.perform(get("/api/classSchedule")
                .param("courseId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].classId").value(1))
                .andExpect(jsonPath("$[0].courseId").value(1))
                .andExpect(jsonPath("$[1].classId").value(2))
                .andExpect(jsonPath("$[1].courseId").value(1));
        
        // Verify the service was called
        verify(classScheduleService).getAllClassSchedulesForCourse(1);
    }

    @Test
    void getClassScheduleById() throws Exception {
        // Mock the service and mapper
        when(classScheduleService.getClassScheduleById(1)).thenReturn(classSchedule1);
        when(modelMapper.map(classSchedule1, ClassScheduleDTO.class)).thenReturn(classScheduleDTO1);
        
        // Perform the request
        mockMvc.perform(get("/api/classSchedule/classId/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classId").value(1))
                .andExpect(jsonPath("$.courseId").value(1))
                .andExpect(jsonPath("$.dayOfWeek").value("Monday"))
                .andExpect(jsonPath("$.startTime").value("09:00:00"))
                .andExpect(jsonPath("$.endTime").value("11:00:00"));
        
        // Verify the service was called
        verify(classScheduleService).getClassScheduleById(1);
    }

    @Test
    void addClassSchedule() throws Exception {
        // Create a new classSchedule and DTO for the request
        ClassSchedule newClassSchedule = new ClassSchedule(
            4, 
            course1, 
            "Wednesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            25, 
            25
        );
        
        ClassScheduleDTO newClassScheduleDTO = new ClassScheduleDTO(
            4, 
            1, 
            "Test Course", 
            "CS101", 
            "Wednesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            25, 
            25
        );
        
        // Mock the mapper and service
        when(modelMapper.map(any(ClassScheduleDTO.class), eq(ClassSchedule.class))).thenReturn(newClassSchedule);
        when(classScheduleService.addClassSchedule(any(ClassSchedule.class))).thenReturn(newClassSchedule);
        when(modelMapper.map(newClassSchedule, ClassScheduleDTO.class)).thenReturn(newClassScheduleDTO);
        
        // Perform the request
        mockMvc.perform(post("/api/classSchedule/addClassSchedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClassScheduleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.classId").value(4))
                .andExpect(jsonPath("$.courseId").value(1))
                .andExpect(jsonPath("$.dayOfWeek").value("Wednesday"));
        
        // Verify the service was called
        verify(classScheduleService).addClassSchedule(any(ClassSchedule.class));
    }

    @Test
    void editClassSchedule() throws Exception {       
        ClassSchedule updatedClassSchedule = new ClassSchedule(
            1, 
            course1, 
            "Thursday",  // Changed day
            LocalTime.of(10, 0),  // Changed time
            LocalTime.of(12, 0),  // Changed time
            25,                   // Changed capacity
            25                    // Changed vacancy
        );
        
        ClassScheduleDTO updatedClassScheduleDTO = new ClassScheduleDTO(
            1, 
            1, 
            "Test Course", 
            "CS101", 
            "Thursday", 
            LocalTime.of(10, 0), 
            LocalTime.of(12, 0), 
            25, 
            25
        );
        
        // Mock service and mapper
        when(classScheduleService.getClassScheduleById(1)).thenReturn(classSchedule1);
        
        // Use doAnswer to properly handle the modelMapper.map behavior
        doAnswer(invocation -> {
            ClassScheduleDTO source = invocation.getArgument(0);
            ClassSchedule target = invocation.getArgument(1);
            
            // Update the target with values from source
            target.setDayOfWeek(source.getDayOfWeek());
            target.setStartTime(source.getStartTime());
            target.setEndTime(source.getEndTime());
            target.setMaxCapacity(source.getMaxCapacity());
            target.setVacancy(source.getVacancy());
            
            return null;
        }).when(modelMapper).map(any(ClassScheduleDTO.class), any(ClassSchedule.class));
        
        when(classScheduleService.editClassSchedule(any(ClassSchedule.class))).thenReturn(updatedClassSchedule);
        when(modelMapper.map(updatedClassSchedule, ClassScheduleDTO.class)).thenReturn(updatedClassScheduleDTO);
        
        // Perform the request
        mockMvc.perform(put("/api/classSchedule/editClassSchedule/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClassScheduleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classId").value(1))
                .andExpect(jsonPath("$.startTime").value("10:00:00"))
                .andExpect(jsonPath("$.endTime").value("12:00:00"))
                .andExpect(jsonPath("$.maxCapacity").value(25));
        
        // Verify service calls
        verify(classScheduleService).getClassScheduleById(1);
        verify(classScheduleService).editClassSchedule(any(ClassSchedule.class));
    }

    @Test
    void editClassScheduleNotFound() throws Exception {
        ClassScheduleDTO updatedClassScheduleDTO = new ClassScheduleDTO(
            999, 
            1, 
            "Test Course", 
            "CS101", 
            "Monday", 
            LocalTime.of(10, 0), 
            LocalTime.of(12, 0), 
            25, 
            25
        );
        
        // Mock service to return null (class not found)
        when(classScheduleService.getClassScheduleById(999)).thenReturn(null);
        
        // Perform the request
        mockMvc.perform(put("/api/classSchedule/editClassSchedule/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedClassScheduleDTO)))
                .andExpect(status().isNotFound());
        
        // Verify service was called but editClassSchedule was not
        verify(classScheduleService).getClassScheduleById(999);
        verify(classScheduleService, never()).editClassSchedule(any(ClassSchedule.class));
    }
    @Test
    void testInvalidIdFormat() throws Exception {
        // Test with non-numeric ID
        mockMvc.perform(get("/api/classSchedule/classId/abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetClassScheduleNotFound() throws Exception {
        // Mock service to throw ResourceNotFoundException
        when(classScheduleService.getClassScheduleById(999)).thenThrow(
                new ResourceNotFoundException("Class Schedule", "classId", "999"));
        
        // Perform the request
        mockMvc.perform(get("/api/classSchedule/classId/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testAddClassScheduleWithInvalidCapacity() throws Exception {
        // Create DTO with invalid capacity (vacancy > maxCapacity)
        ClassScheduleDTO invalidCapacityDTO = new ClassScheduleDTO(
            null, 
            1, 
            "Test Course", 
            "CS101", 
            "Wednesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            20,  // maxCapacity 
            25   // vacancy > maxCapacity
        );
        
        // Create corresponding model object
        ClassSchedule invalidCapacitySchedule = new ClassSchedule(
            null, 
            course1, 
            "Wednesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            20, 
            25
        );
        
        // Mock the mapper and service
        when(modelMapper.map(any(ClassScheduleDTO.class), eq(ClassSchedule.class))).thenReturn(invalidCapacitySchedule);
        when(classScheduleService.addClassSchedule(any(ClassSchedule.class)))
            .thenThrow(new InvalidCapacityException("Vacancy cannot be more than max capacity"));
        
        // Perform the request
        mockMvc.perform(post("/api/classSchedule/addClassSchedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCapacityDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Vacancy cannot be more than max capacity"));
    }
    
    @Test
    void testAddClassScheduleWithNegativeCapacity() throws Exception {
        // Create DTO with negative capacity
        ClassScheduleDTO negativeCapacityDTO = new ClassScheduleDTO(
            null, 
            1, 
            "Test Course", 
            "CS101", 
            "Wednesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            -10,  // negative maxCapacity 
            0     // valid vacancy
        );
        
        // Create corresponding model object
        ClassSchedule negativeCapacitySchedule = new ClassSchedule(
            null, 
            course1, 
            "Wednesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            -10, 
            0
        );
        
        // Mock the mapper and service
        when(modelMapper.map(any(ClassScheduleDTO.class), eq(ClassSchedule.class))).thenReturn(negativeCapacitySchedule);
        when(classScheduleService.addClassSchedule(any(ClassSchedule.class)))
            .thenThrow(new InvalidCapacityException("Capacity must be a positive number"));
        
        // Perform the request
        mockMvc.perform(post("/api/classSchedule/addClassSchedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(negativeCapacityDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Capacity must be a positive number"));
    }
    
    @Test
    void testAddClassScheduleWithInvalidTimeRange() throws Exception {
        // Create DTO with invalid time range (start after end)
        ClassScheduleDTO invalidTimeDTO = new ClassScheduleDTO(
            null, 
            1, 
            "Test Course", 
            "CS101", 
            "Wednesday", 
            LocalTime.of(15, 0),  // start time after end time
            LocalTime.of(13, 0), 
            20, 
            20
        );
        
        // Create corresponding model object
        ClassSchedule invalidTimeSchedule = new ClassSchedule(
            null, 
            course1, 
            "Wednesday", 
            LocalTime.of(15, 0),  // start time after end time
            LocalTime.of(13, 0), 
            20, 
            20
        );
        
        // Mock the mapper and service
        when(modelMapper.map(any(ClassScheduleDTO.class), eq(ClassSchedule.class))).thenReturn(invalidTimeSchedule);
        when(classScheduleService.addClassSchedule(any(ClassSchedule.class)))
            .thenThrow(new InvalidDateException("Start date cannot be after end date"));
        
        // Perform the request
        mockMvc.perform(post("/api/classSchedule/addClassSchedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTimeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Start date cannot be after end date"));
    }
    
    @Test
    void testAddClassScheduleDuplicateSchedule() throws Exception {
        // Create a duplicate schedule DTO
        ClassScheduleDTO duplicateDTO = new ClassScheduleDTO(
            null, 
            1, 
            "Test Course", 
            "CS101", 
            "Monday", 
            LocalTime.of(9, 0), 
            LocalTime.of(11, 0), 
            30, 
            30
        );
        
        // Create corresponding model object
        ClassSchedule duplicateSchedule = new ClassSchedule(
            null, 
            course1, 
            "Monday", 
            LocalTime.of(9, 0), 
            LocalTime.of(11, 0), 
            30, 
            30
        );
        
        // Mock the mapper and service
        when(modelMapper.map(any(ClassScheduleDTO.class), eq(ClassSchedule.class))).thenReturn(duplicateSchedule);
        when(classScheduleService.addClassSchedule(any(ClassSchedule.class)))
            .thenThrow(new DuplicateIDException(duplicateSchedule.toString()));
        
        // Perform the request
        mockMvc.perform(post("/api/classSchedule/addClassSchedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void testEditClassScheduleInvalidCapacity() throws Exception {
        // Create update DTO with invalid capacity
        ClassScheduleDTO invalidCapacityDTO = new ClassScheduleDTO(
            1, 
            1, 
            "Test Course", 
            "CS101", 
            "Monday", 
            LocalTime.of(9, 0), 
            LocalTime.of(11, 0), 
            25,  // maxCapacity
            30   // vacancy > maxCapacity
        );
        
        // Mock get class schedule
        when(classScheduleService.getClassScheduleById(1)).thenReturn(classSchedule1);
        
        // Mock service to throw exception
        when(classScheduleService.editClassSchedule(any(ClassSchedule.class)))
            .thenThrow(new InvalidCapacityException("Vacancy cannot be more than max capacity"));
        
        // Perform the request
        mockMvc.perform(put("/api/classSchedule/editClassSchedule/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCapacityDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Vacancy cannot be more than max capacity"));
    }
    
    @Test
    void testEditClassScheduleInvalidTimeRange() throws Exception {
        // Create update DTO with invalid time range
        ClassScheduleDTO invalidTimeDTO = new ClassScheduleDTO(
            1, 
            1, 
            "Test Course", 
            "CS101", 
            "Monday", 
            LocalTime.of(13, 0),  // start after end
            LocalTime.of(11, 0), 
            30, 
            30
        );
        
        // Mock get class schedule
        when(classScheduleService.getClassScheduleById(1)).thenReturn(classSchedule1);
        
        // Mock service to throw exception
        when(classScheduleService.editClassSchedule(any(ClassSchedule.class)))
            .thenThrow(new InvalidDateException("Start date cannot be after end date"));
        
        // Perform the request
        mockMvc.perform(put("/api/classSchedule/editClassSchedule/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTimeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("Start date cannot be after end date"));
    }
    
    @Test
    void testGetAllClassSchedulesForNonExistentCourse() throws Exception {
        // Mock service to throw exception for non-existent course
        when(classScheduleService.getAllClassSchedulesForCourse(999))
            .thenThrow(new ResourceNotFoundException("Course", "courseId", "999"));
        
        // Perform the request
        mockMvc.perform(get("/api/classSchedule")
                .param("courseId", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
    
    @Test
    void testEditClassScheduleDuplicateSchedule() throws Exception {
        // Create update DTO that would result in duplicate
        ClassScheduleDTO duplicateDTO = new ClassScheduleDTO(
            1, 
            1, 
            "Test Course", 
            "CS101", 
            "Tuesday",  // Already exists for classSchedule2
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            30, 
            30
        );
        
        // Mock get class schedule
        when(classScheduleService.getClassScheduleById(1)).thenReturn(classSchedule1);
        
        // Mock service to throw exception for duplicate
        when(classScheduleService.editClassSchedule(any(ClassSchedule.class)))
            .thenThrow(new DuplicateIDException(classSchedule1.toString()));
        
        // Perform the request
        mockMvc.perform(put("/api/classSchedule/editClassSchedule/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void testInternalServerError() throws Exception {
        // Create a test DTO
        ClassScheduleDTO testDTO = new ClassScheduleDTO(
            null, 
            1, 
            "Test Course", 
            "CS101", 
            "Wednesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            25, 
            25
        );
        
        // Create corresponding model object
        ClassSchedule testSchedule = new ClassSchedule(
            null, 
            course1, 
            "Wednesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            25, 
            25
        );
        
        // Mock the mapper and service
        when(modelMapper.map(any(ClassScheduleDTO.class), eq(ClassSchedule.class))).thenReturn(testSchedule);
        when(classScheduleService.addClassSchedule(any(ClassSchedule.class)))
            .thenThrow(new RuntimeException("Unexpected server error"));
        
        // Perform the request
        mockMvc.perform(post("/api/classSchedule/addClassSchedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDTO)))
                .andExpect(status().isInternalServerError());
    }
}