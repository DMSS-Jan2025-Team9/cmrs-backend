package com.example.courserecommendation.model;

import jakarta.persistence.*;

@Entity
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendationId;

    private Long programId;
    private Long courseId;

    // Constructors
    public Recommendation() {
    }

    public Recommendation(Long programId, Long courseId) {
        this.programId = programId;
        this.courseId = courseId;
    }

    // Getters and Setters
    public Long getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(Long recommendationId) {
        this.recommendationId = recommendationId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
