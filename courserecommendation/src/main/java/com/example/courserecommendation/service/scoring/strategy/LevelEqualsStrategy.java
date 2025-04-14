package com.example.courserecommendation.service.scoring.strategy;

import org.springframework.stereotype.Component;

import com.example.courserecommendation.dto.CourseDTO;
import com.example.courserecommendation.model.ProgramRecommendationRule;
import com.example.courserecommendation.service.scoring.RecommendationRuleStrategy;

@Component
public class LevelEqualsStrategy implements RecommendationRuleStrategy {

    @Override
    public boolean matches(CourseDTO course, ProgramRecommendationRule rule) {
        return course.getLevel().equalsIgnoreCase(rule.getValue());
    }

    @Override
    public double applyScore(CourseDTO course, ProgramRecommendationRule rule) {
        return matches(course, rule) ? rule.getWeight() : 0.0;
    }
    
}
