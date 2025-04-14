package com.example.courserecommendation.service.scoring.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.courserecommendation.service.scoring.RecommendationRuleStrategy;
import com.example.courserecommendation.service.scoring.strategy.CategoryEqualsStrategy;
import com.example.courserecommendation.service.scoring.strategy.LevelEqualsStrategy;
import com.example.courserecommendation.service.scoring.strategy.TitleContainsStrategy;

/*
 * Factory Pattern: To fetch the correct strategy implementation for a rule type
 */
@Component
public class RecommendationRuleStrategyFactory {
    private final Map<String, RecommendationRuleStrategy> strategyMap = new HashMap<>();

     @Autowired
    public RecommendationRuleStrategyFactory(List<RecommendationRuleStrategy> strategies) {
        for (RecommendationRuleStrategy strategy : strategies) {
            if (strategy instanceof TitleContainsStrategy) {
                strategyMap.put("TITLE_CONTAINS", strategy);
            } else if (strategy instanceof LevelEqualsStrategy) {
                strategyMap.put("LEVEL_EQUALS", strategy);
            } else if (strategy instanceof CategoryEqualsStrategy) {
                strategyMap.put("CATEGORY_EQUALS", strategy);
            }
        }
    }

    public RecommendationRuleStrategy getStrategy(String ruleType) {
        RecommendationRuleStrategy strategy = strategyMap.get(ruleType.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown rule type: " + ruleType);
        }
        return strategy;
    }

}
