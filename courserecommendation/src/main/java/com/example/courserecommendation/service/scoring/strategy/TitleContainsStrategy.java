package com.example.courserecommendation.service.scoring.strategy;

import org.springframework.stereotype.Component;

import com.example.courserecommendation.dto.CourseDTO;
import com.example.courserecommendation.model.ProgramRecommendationRule;
import com.example.courserecommendation.service.scoring.RecommendationRuleStrategy;

@Component
public class TitleContainsStrategy implements RecommendationRuleStrategy  {
    @Override
    public boolean matches(CourseDTO course, ProgramRecommendationRule rule) {
        return course.getCourseName().toLowerCase().contains(rule.getValue().toLowerCase());
    }

    @Override
    public double applyScore(CourseDTO course, ProgramRecommendationRule rule) {
        return matches(course, rule) ? rule.getWeight() : 0.0;
    }
}
