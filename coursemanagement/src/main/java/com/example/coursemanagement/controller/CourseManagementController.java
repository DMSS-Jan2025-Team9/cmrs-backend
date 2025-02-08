package com.example.coursemanagement.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/courses")
public class CourseManagementController {

    @GetMapping("/getCourse")
    public String getCourse(@RequestParam String courseName) {
        return "Getting course details for: " + courseName;
    }
    
    @GetMapping("/getCourses")
    public String getCourses() {
        return "Getting list of courses";
    }   
    
}

