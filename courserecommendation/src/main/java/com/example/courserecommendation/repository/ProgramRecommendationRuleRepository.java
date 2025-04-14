package com.example.courserecommendation.repository;

import com.example.courserecommendation.model.ProgramRecommendationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProgramRecommendationRuleRepository extends JpaRepository<ProgramRecommendationRule, Long> {
    
    List<ProgramRecommendationRule> findByProgramId(Long programId);

    void deleteByProgramId(Long programId);
    
}
