package com.example.courserecommendation.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/courseRecommendation")
public class CourseRecommendationController {
    
    @GetMapping("/getCourses")
    public String getCourses() {
        return "Getting list of courses to recommend";
    }   
    
}

