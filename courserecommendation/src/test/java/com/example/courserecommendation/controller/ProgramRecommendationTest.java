package com.example.courserecommendation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.courserecommendation.constants.RecommendationConstants;
import com.example.courserecommendation.dto.ClassScheduleDTO;
import com.example.courserecommendation.dto.CourseDTO;
import com.example.courserecommendation.dto.CourseScoreDTO;
import com.example.courserecommendation.dto.ProgramRecommendationRuleDTO;
import com.example.courserecommendation.model.ProgramRecommendationRule;
import com.example.courserecommendation.service.ProgramRecommendationService;
import com.example.courserecommendation.service.registry.ProgramRuleRegistry;
import com.example.courserecommendation.service.scoring.RecommendationScoringEngine;
import com.example.courserecommendation.webclient.CourseClient;

@SpringBootTest
@AutoConfigureMockMvc
public class ProgramRecommendationTest {
    
    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private CourseClient courseClient;

    @MockitoBean
    private ProgramRuleRegistry ruleRegistry;

    @MockitoBean
    private RecommendationScoringEngine scoringEngine;

    @InjectMocks
    private ProgramRecommendationService recommendationService;

    private CourseDTO course1;
    private CourseDTO course2;
    private ProgramRecommendationRule rule1;
    private ProgramRecommendationRule rule2;
    private List<CourseDTO> mockCourses;
    private List<ProgramRecommendationRule> mockRules;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course1 = new CourseDTO(
            101,
            "Java Fundamentals",
            "CS101",
            "Intro to Java programming",
            "Programming",
            "Beginner"
        );

        course2 = new CourseDTO(
            102,
            "Machine Learning",
            "CS202",
            "Intro to ML and algorithms",
            "AI",
            "Intermediate"
        );

        rule1 = new ProgramRecommendationRule(
            1L,                        // programId
            "category",               // type
            "Programming",            // value
            1.0                       // weight
        );

        rule2 = new ProgramRecommendationRule(
            1L,
            "level",
            "Beginner",
            0.5
        );
    }

    @Test
    void testSetRules_shouldStoreRulesInRegistry() {

        recommendationService.updateRules(1L, mockRules);

        verify(ruleRegistry).updateRules(eq(100L), anyList());

        // // Create a list with only the ProgramRecommendationRule
        // List<ProgramRecommendationRule> mockRules = new ArrayList<>();
        // mockRules.add(rule1);
        // mockRules.add(rule2);
        
        // // Mock the service to return only these class schedules
        // when(recommendationService.getRulesForProgram(1L)).thenReturn(mockRules);
        
        // // Mock the mapper to return the corresponding DTOs
        // when(modelMapper.map(mockRules, ProgramRecommendationRuleDTO.class)).thenReturn(rule1);
        // when(modelMapper.map(mockRules, ProgramRecommendationRuleDTO.class)).thenReturn(classScheduleDTO2);
        
        // // Perform the request
        // mockMvc.perform(get("/{programId}/rules")
        //         .param("programId", "1")
        //         .contentType(MediaType.APPLICATION_JSON))
        //         .andExpect(status().isOk())
        //         .andExpect(jsonPath("$", hasSize(2)))
        //         .andExpect(jsonPath("$[0].classId").value(1))
        //         .andExpect(jsonPath("$[0].courseId").value(1))
        //         .andExpect(jsonPath("$[1].classId").value(2))
        //         .andExpect(jsonPath("$[1].courseId").value(1));
        
        // // Verify the service was called
        // verify(recommendationService).getRulesForProgram(1L);

    }

    @Test
    void testGetRules_shouldReturnRuleDTOs() {
        List<ProgramRecommendationRule> rules = mockRules;
        when(ruleRegistry.getRules(1L)).thenReturn(rules);

        List<ProgramRecommendationRuleDTO> result = recommendationService.getRulesForProgram(1L);

        assertEquals(1, result.size());
        assertEquals("category", result.get(0).getType());
        assertEquals("Programming", result.get(0).getValue());
        assertEquals(1.0, result.get(0).getWeight());
    }

    @Test
    void testGetRecommendedCourses_shouldReturnScoredCourses() {
        when(courseClient.getAllCourses()).thenReturn(List.of(course1, course2));
        when(ruleRegistry.getRules(1L)).thenReturn(mockRules);

        when(scoringEngine.scoreCourse(course1, mockRules)).thenReturn(9.0);
        when(scoringEngine.scoreCourse(course2, mockRules)).thenReturn(0.0);

        List<CourseScoreDTO> result = recommendationService.getRecommendedCourses(100L);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCourseId());
        assertEquals("Java Fundamentals", result.get(0).getCourseName());
        assertEquals(9.0, result.get(0).getScore());
    }

    @Test
    void testGetRecommendedCourseSchedule_shouldReturnScheduleDTOs() {
        when(courseClient.getAllCourses()).thenReturn(List.of(course1, course2));
        when(ruleRegistry.getRules(1L)).thenReturn(mockRules);

        when(scoringEngine.scoreCourse(course1, mockRules)).thenReturn(7.5);
        when(scoringEngine.scoreCourse(course2, mockRules)).thenReturn(0.0);

        List<ClassScheduleDTO> result = recommendationService.getRecommendedCourseSchedule(1L);

        assertEquals(1, result.size());
        ClassScheduleDTO dto = result.get(0);

        assertEquals(course1.getCourseId(), dto.getCourseId());
        assertEquals("Java Fundamentals", dto.getCourseName());
    }
}