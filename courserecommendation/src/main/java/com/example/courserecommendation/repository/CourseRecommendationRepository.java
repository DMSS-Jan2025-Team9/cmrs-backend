package com.example.courserecommendation.repository;

import com.example.courserecommendation.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByProgramId(Long programId);
}
