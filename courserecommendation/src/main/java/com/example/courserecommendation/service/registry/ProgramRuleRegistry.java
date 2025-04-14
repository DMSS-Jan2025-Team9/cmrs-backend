package com.example.courserecommendation.service.registry;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.courserecommendation.model.ProgramRecommendationRule;
import com.example.courserecommendation.repository.ProgramRecommendationRuleRepository;

/*
 * Abstraction layer - Can switch to in-memory/mock or cached version easily
 * to inject ProgramRuleRegistry anywhere and keep the rule-fetching logic abstracted away from raw repository access.
 */
@Component
public class ProgramRuleRegistry {

    private final ProgramRecommendationRuleRepository ruleRepository;

    public ProgramRuleRegistry(ProgramRecommendationRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<ProgramRecommendationRule> getRules(Long programId) {
        return ruleRepository.findByProgramId(programId);
    }

    public void updateRules(Long programId, List<ProgramRecommendationRule> rules) {
        ruleRepository.deleteByProgramId(programId);

        rules.forEach(rule -> rule.setProgramId(programId));
        ruleRepository.saveAll(rules);
    }
}
