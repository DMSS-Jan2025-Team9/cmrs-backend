package com.example.courserecommendation.service.scoring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.courserecommendation.dto.CourseDTO;
import com.example.courserecommendation.model.ProgramRecommendationRule;
import com.example.courserecommendation.service.scoring.factory.RecommendationRuleStrategyFactory;

@Component
public class RecommendationScoringEngine {
    private final RecommendationRuleStrategyFactory strategyFactory;

    @Autowired
    public RecommendationScoringEngine(RecommendationRuleStrategyFactory factory) {
        this.strategyFactory = factory;
    }
    
    /**
     * Scores a given course based on the rules for a specific program.
     *
     * @param course The course to be scored.
     * @param rules The rules that should be applied to the course.
     * @return The total score of the course.
     */
    public double scoreCourse(CourseDTO course, List<ProgramRecommendationRule> rules) {
        double totalScore = 0.0;

        for (ProgramRecommendationRule rule : rules) {
            // Get the appropriate strategy based on the rule type
            RecommendationRuleStrategy strategy = getStrategyForRule(rule);

            // Apply the strategy and accumulate the score
            totalScore += strategy.applyScore(course, rule);
        }

        return totalScore;
    }

    /**
     * Retrieves the appropriate rule strategy for a given rule type.
     * 
     * @param rule The program recommendation rule.
     * @return The strategy associated with the rule.
     */
    private RecommendationRuleStrategy getStrategyForRule(ProgramRecommendationRule rule) {
        // The strategy factory will fetch the appropriate strategy based on the rule type
        // This assumes a StrategyFactory has already been defined and injected
        return strategyFactory.getStrategy(rule.getType());
    }
}
