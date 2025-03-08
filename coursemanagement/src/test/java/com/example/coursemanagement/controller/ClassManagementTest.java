package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.ClassScheduleDTO;
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

    private Course course;
    private ClassSchedule classSchedule;
    private ClassScheduleDTO classScheduleDTO;
    private List<ClassSchedule> classScheduleList;
    private List<ClassScheduleDTO> classScheduleDTOList;

    @BeforeEach
    void setUp() {
        // Setup test data
        course = new Course(1, "Test Course", "CS101", null, null, 100, "Open", "Test Description");
        
        classSchedule = new ClassSchedule(
            1, 
            course, 
            "Monday", 
            LocalTime.of(9, 0), 
            LocalTime.of(11, 0), 
            30, 
            30
        );
        
        classScheduleDTO = new ClassScheduleDTO(
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
        
        // Create list of class schedules
        classScheduleList = new ArrayList<>();
        classScheduleList.add(classSchedule);
        
        classScheduleDTOList = new ArrayList<>();
        classScheduleDTOList.add(classScheduleDTO);
    }

    @Test
    void getAllClassSchedulesForCourse() throws Exception {
        // Mock the service and mapper
        when(classScheduleService.getAllClassSchedulesForCourse(1)).thenReturn(classScheduleList);
        when(modelMapper.map(any(ClassSchedule.class), eq(ClassScheduleDTO.class))).thenReturn(classScheduleDTO);
        
        // Perform the request
        mockMvc.perform(get("/api/classSchedule")
                .param("courseId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].classId").value(1))
                .andExpect(jsonPath("$[0].courseId").value(1))
                .andExpect(jsonPath("$[0].dayOfWeek").value("Monday"));
        
        // Verify the service was called
        verify(classScheduleService).getAllClassSchedulesForCourse(1);
    }

    @Test
    void getClassScheduleById() throws Exception {
        // Mock the service and mapper
        when(classScheduleService.getClassScheduleById(1)).thenReturn(classSchedule);
        when(modelMapper.map(classSchedule, ClassScheduleDTO.class)).thenReturn(classScheduleDTO);
        
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
            2, 
            course, 
            "Wednesday", 
            LocalTime.of(13, 0), 
            LocalTime.of(15, 0), 
            25, 
            25
        );
        
        ClassScheduleDTO newClassScheduleDTO = new ClassScheduleDTO(
            2, 
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
                .andExpect(jsonPath("$.classId").value(2))
                .andExpect(jsonPath("$.courseId").value(1))
                .andExpect(jsonPath("$.dayOfWeek").value("Wednesday"));
        
        // Verify the service was called
        verify(classScheduleService).addClassSchedule(any(ClassSchedule.class));
    }

    @Test
    void editClassSchedule() throws Exception {
        // Create updated class schedule
        ClassSchedule existingClassSchedule = new ClassSchedule(
            1, 
            course, 
            "Monday", 
            LocalTime.of(9, 0), 
            LocalTime.of(11, 0), 
            30, 
            30
        );
        
        ClassSchedule updatedClassSchedule = new ClassSchedule(
            1, 
            course, 
            "Monday", 
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
            "Monday", 
            LocalTime.of(10, 0), 
            LocalTime.of(12, 0), 
            25, 
            25
        );
        
        // Mock service and mapper
        when(classScheduleService.getClassScheduleById(1)).thenReturn(existingClassSchedule);
        
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
}