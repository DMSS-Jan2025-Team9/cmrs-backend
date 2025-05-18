package com.example.courserecommendation.service.scoring.strategy;

import org.springframework.stereotype.Component;

import com.example.courserecommendation.dto.CourseDTO;

@Component
class ProgramBasedRecommendation implements IRecommendationStrategy {
    private RecommendationRepository repo;
    public List<CourseDTO> recommend(StudentDTO student) {
        var recs = repo.getByProgramId(student.getEnrolledProgramId());
        return recs.stream()
                   .map(r -> repo.getCourseById(r.getCourseId()))
                   .collect(Collectors.toList());
    }
}
