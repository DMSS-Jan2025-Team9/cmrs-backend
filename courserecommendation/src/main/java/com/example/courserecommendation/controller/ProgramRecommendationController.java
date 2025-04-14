package com.example.courserecommendation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courserecommendation.dto.ClassScheduleDTO;
import com.example.courserecommendation.dto.CourseScoreDTO;
import com.example.courserecommendation.dto.ProgramRecommendationRuleDTO;
import com.example.courserecommendation.model.ProgramRecommendationRule;
import com.example.courserecommendation.service.ProgramRecommendationService;

@RestController
@RequestMapping("/api/courseRecommendation")
public class ProgramRecommendationController {

    private final ProgramRecommendationService recommendationService;

    public ProgramRecommendationController(ProgramRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Admin: Set recommendation rules for a specific program ID.
     *
     * @param programId The ID of the program.
     * @param rules The list of rules to be set.
     * @return ResponseEntity with status code.
     */
    @PostMapping("/{programId}/rules")
    public ResponseEntity<String> setRules(
            @PathVariable Long programId,
            @RequestBody List<ProgramRecommendationRule> rules
    ) {
        try {
            recommendationService.updateRules(programId, rules); // Sets the rules for the program
            return new ResponseEntity<>("Rules set successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error setting rules: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * --- Admin: View Recommendation Rules for a specific Program ---
     *
     * @param programId The ID of the program.
     * @return List of rules for the program.
     */
    @GetMapping("/{programId}/rules")
    public ResponseEntity<List<ProgramRecommendationRuleDTO>> getRules(@PathVariable Long programId) {
        try {
            List<ProgramRecommendationRuleDTO> rules = recommendationService.getRulesForProgram(programId);
            if (rules.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // No content available for recommendations
            }
            return new ResponseEntity<>(rules, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Student: Get recommended courses for a specific program ID.
     *
     * @param programId The ID of the program.
     * @return List of recommended courses with scores.
     */
    @GetMapping("/{programId}/recommended-courses")
    public ResponseEntity<List<CourseScoreDTO>> getRecommendedCourses(@PathVariable Long programId) {
        try {
            List<CourseScoreDTO> recommendedCourses = recommendationService.getRecommendedCourses(programId);
            if (recommendedCourses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // No content available for recommendations
            }
            return new ResponseEntity<>(recommendedCourses, HttpStatus.OK); // Return the list of recommended courses
        } catch (Exception e) {
            // Log the exception (optional)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Internal server error
        }
    }

    
    /**
     * Get recommended course schedule for a specific program ID.
     *
     * @param programId The ID of the program.
     * @return List of course schedules.
     */
    @GetMapping("/{programId}/recommended-course-schedule")
    public ResponseEntity<List<ClassScheduleDTO>> getRecommendedCourseSchedule(@PathVariable Long programId) {
        try {
            List<ClassScheduleDTO> recommendedCourseSchedule = recommendationService.getRecommendedCourseSchedule(programId);
            if (recommendedCourseSchedule.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(recommendedCourseSchedule, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
    


