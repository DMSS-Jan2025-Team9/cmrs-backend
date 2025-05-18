package com.example.courserecommendation.service.scoring;

import com.example.courserecommendation.dto.CourseDTO;
import com.example.courserecommendation.model.ProgramRecommendationRule;

/*
 * Strategy pattern - Handle multiple recommendation strategies (e.g., keyword-based, program based) for course recommendation
 */
public interface IRecommendationStrategy  {
    
    List<Course> recommend(StudentDTO student);

}