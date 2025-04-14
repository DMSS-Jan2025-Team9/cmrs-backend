package com.example.courserecommendation.service.scoring;

import com.example.courserecommendation.dto.CourseDTO;
import com.example.courserecommendation.model.ProgramRecommendationRule;

/*
 * Strategy pattern - Handle multiple scoring strategies (e.g., keyword-based, tag-based) for course recommendation
 */
public interface RecommendationRuleStrategy {

    boolean matches(CourseDTO course, ProgramRecommendationRule rule);

    double applyScore(CourseDTO course, ProgramRecommendationRule rule);

}