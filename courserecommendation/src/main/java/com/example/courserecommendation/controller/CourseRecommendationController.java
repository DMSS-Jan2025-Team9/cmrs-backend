package com.example.courserecommendation.controller;

import com.example.courserecommendation.model.Recommendation;
import com.example.courserecommendation.repository.CourseRecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courseRecommendation")
public class CourseRecommendationController {
    
    @Autowired
    private CourseRecommendationRepository courseRecommendationRepository;

    // Get all recommendations
    @GetMapping
    public List<Recommendation> getRecommendations() {
        return courseRecommendationRepository.findAll();
    }

    // Get recommendations by program ID
    @GetMapping("/{programId}")
    public List<Recommendation> getRecommendation(@PathVariable Long programId) {
        return courseRecommendationRepository.findByProgramId(programId);
    }

}
    


