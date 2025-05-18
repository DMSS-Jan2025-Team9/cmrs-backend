package com.example.courserecommendation.service.scoring.strategy;

import org.springframework.stereotype.Component;


@Component
class KeywordMatchRecommendation implements IRecommendationStrategy {
    private List<Course> allCourses;
    public List<Course> recommend(StudentDTO student) {
        return allCourses.stream()
                         .filter(c -> c.getCourseName().toLowerCase().contains(student.getSearchKeyword().toLowerCase())
                                  || c.getDescription().toLowerCase().contains(student.getSearchKeyword().toLowerCase()))
                         .collect(Collectors.toList());
    }
}
