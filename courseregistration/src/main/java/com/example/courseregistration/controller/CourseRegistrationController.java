package com.example.courseregistration.controller;

import org.springframework.web.bind.annotation.*;

import com.example.courseregistration.model.Course;

@RestController
@RequestMapping("/api/courseRegistration")
public class CourseRegistrationController {

    @PostMapping("/registerCourse")
    public String registerCourse(@RequestBody Course course) {
        return "Registering for course: " + course.getCourseName();
    }
   
    
}

