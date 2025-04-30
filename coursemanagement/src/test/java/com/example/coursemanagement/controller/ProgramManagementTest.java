package com.example.coursemanagement.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.dto.ProgramDto;
import com.example.coursemanagement.service.ProgramService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProgramManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgramService programService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProgramDto program1;
    private ProgramDto program2;
    private List<ProgramDto> programs;

    private CourseDTO courseDTO1;
    private CourseDTO courseDTO2;
    private List<CourseDTO> coursesForProgram1;

    @BeforeEach
    void setUp() {
        // Setup test data
        program1 = new ProgramDto(1, "Computer Science", "Bachelor's program in Computer Science", null);
        program2 = new ProgramDto(2, "Data Science", "Master's program in Data Science", null);
        programs = Arrays.asList(program1, program2);

        courseDTO1 = new CourseDTO(1, "Introduction to Programming", "CS101", null, null, 100, "Open", "Basic programming concepts", 1);
        courseDTO2 = new CourseDTO(2, "Data Structures", "CS201", null, null, 50, "Open", "Advanced programming concepts", 1);
        coursesForProgram1 = Arrays.asList(courseDTO1, courseDTO2);
    }

    @Test
    void testGetProgramById_Success() throws Exception {
        // Given
        Integer programId = 1;
        when(programService.getProgramById(programId)).thenReturn(program1);

        // When & Then
        mockMvc.perform(get("/api/program/{programId}", programId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.programId").value(program1.getProgramId()))
                .andExpect(jsonPath("$.programName").value(program1.getProgramName()))
                .andExpect(jsonPath("$.programDesc").value(program1.getProgramDesc()));

        verify(programService, times(1)).getProgramById(programId);
    }

    @Test
    void testGetProgramById_NotFound() throws Exception {
        // Given
        Integer programId = 999;
        when(programService.getProgramById(programId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/program/{programId}", programId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));  

        verify(programService, times(1)).getProgramById(programId);
    }

    @Test
    void testGetAllPrograms_Success() throws Exception {
        // Given
        when(programService.getAllPrograms()).thenReturn(programs);

        // When & Then
        mockMvc.perform(get("/api/program"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].programId").value(programs.get(0).getProgramId()))
                .andExpect(jsonPath("$[0].programName").value(programs.get(0).getProgramName()))
                .andExpect(jsonPath("$[1].programId").value(programs.get(1).getProgramId()))
                .andExpect(jsonPath("$[1].programName").value(programs.get(1).getProgramName()));

        verify(programService, times(1)).getAllPrograms();
    }

    @Test
    void testGetAllPrograms_EmptyList() throws Exception {
        // Given
        when(programService.getAllPrograms()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/program"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(programService, times(1)).getAllPrograms();
    }

    @Test
    void testGetCoursesByProgramId_Success() throws Exception {
        // Given
        Integer programId = 1;
        when(programService.getCoursesByProgramId(programId)).thenReturn(coursesForProgram1);

        // When & Then
        mockMvc.perform(get("/api/program/{programId}/courses", programId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].courseId").value(coursesForProgram1.get(0).getCourseId()))
                .andExpect(jsonPath("$[0].courseName").value(coursesForProgram1.get(0).getCourseName()))
                .andExpect(jsonPath("$[1].courseId").value(coursesForProgram1.get(1).getCourseId()))
                .andExpect(jsonPath("$[1].courseName").value(coursesForProgram1.get(1).getCourseName()));

        verify(programService, times(1)).getCoursesByProgramId(programId);
    }

    @Test
    void testGetCoursesByProgramId_EmptyCourseList() throws Exception {
        // Given
        Integer programId = 3; // Program with no courses
        when(programService.getCoursesByProgramId(programId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/program/{programId}/courses", programId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(programService, times(1)).getCoursesByProgramId(programId);
    }

    @Test
    void testGetCoursesByProgramId_ProgramNotFound() throws Exception {
        // Given
        Integer programId = 999; // Non-existent program
        when(programService.getCoursesByProgramId(programId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/program/{programId}/courses", programId))
                .andExpect(status().isOk()) 
                .andExpect(content().string(""));  

        verify(programService, times(1)).getCoursesByProgramId(programId);
    }
}