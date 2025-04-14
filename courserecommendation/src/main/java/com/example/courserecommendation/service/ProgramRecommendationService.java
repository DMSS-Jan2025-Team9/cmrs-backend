package com.example.courserecommendation.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courserecommendation.dto.ClassScheduleDTO;
import com.example.courserecommendation.dto.CourseDTO;
import com.example.courserecommendation.dto.CourseScoreDTO;
import com.example.courserecommendation.dto.ProgramRecommendationRuleDTO;
import com.example.courserecommendation.model.ProgramRecommendationRule;
import com.example.courserecommendation.service.registry.ProgramRuleRegistry;
import com.example.courserecommendation.service.scoring.RecommendationScoringEngine;
import com.example.courserecommendation.webclient.CourseClient;

@Service
public class ProgramRecommendationService {
    
    private final ProgramRuleRegistry ruleRegistry;
    private final RecommendationScoringEngine scoringEngine;
    private final CourseClient courseClient; // To fetch CourseDTO from other microservice

    @Autowired
    public ProgramRecommendationService(RecommendationScoringEngine scoringEngine, 
                                        ProgramRuleRegistry ruleRegistry,
                                        CourseClient courseClient) {
        this.ruleRegistry = ruleRegistry;
        this.scoringEngine = scoringEngine;
        this.courseClient = courseClient;
    }

      /**
     * Admin API: Save or update rules for a given program.
     */
    public void updateRules(Long programId, List<ProgramRecommendationRule> rules) {
        ruleRegistry.updateRules(programId, rules);
    }

    /**
     * Admin API: Retrieve current rules for a given program.
     */
    public List<ProgramRecommendationRuleDTO> getRulesForProgram(Long programId) {
        List<ProgramRecommendationRule> rules = ruleRegistry.getRules(programId);
        return rules.stream()
            .map(rule -> new ProgramRecommendationRuleDTO(
                    rule.getRuleId(), 
                    rule.getProgramId(),
                    rule.getType(),
                    rule.getValue(),
                    rule.getWeight()
            ))
            .toList();
    }

    /**
     * Student API: Recommend courses for a student in the given program.
     */
    public List<CourseScoreDTO> getRecommendedCourses(Long programId) {
                
        // 1. Fetch all available courses (using CourseClient which might call an external microservice)
        List<CourseDTO> allCourses = courseClient.getAllCourses();

        // 2. Get the rules for the program
        List<ProgramRecommendationRule> rules = ruleRegistry.getRules(programId);
        
        // 3. Apply scoring logic based on the rules and courses
        return applyRecommendationRules(allCourses, rules);
    }

    /**
     * Student API: Get recommended course schedules in the given program
     */
    public List<ClassScheduleDTO> getRecommendedCourseSchedule(Long programId) {
        // 1. Fetch all available courses (using CourseClient which might call an external microservice)
        List<CourseDTO> allCourses = courseClient.getAllCourses();
    
        // 2. Get the rules configured for this program
        List<ProgramRecommendationRule> rules = ruleRegistry.getRules(programId);
    
        // 3. Apply scoring logic based on the rules and courses
        List<CourseScoreDTO> scoredCourses = applyRecommendationRules(allCourses, rules);
       
        // 4. Generate schedule DTOs from the top scored courses
        return new ArrayList<ClassScheduleDTO>();
    }

    
    private List<CourseScoreDTO> applyRecommendationRules(List<CourseDTO> courses, 
                                                           List<ProgramRecommendationRule> rules) {
        // Logic to apply rules and generate a list of CourseScoreDto
        // Example: Filter courses based on program rules and score them
        return courses.stream()
                      .map(course -> {
                        double totalScore = scoringEngine.scoreCourse(course, rules);
                        return new CourseScoreDTO(course.getCourseId(), course.getCourseName(), totalScore);
                      })
                      .filter(dto -> dto.getScore() > 0)
                      .sorted(Comparator.comparingDouble(CourseScoreDTO::getScore).reversed())
                      .collect(Collectors.toList());
    }
}
