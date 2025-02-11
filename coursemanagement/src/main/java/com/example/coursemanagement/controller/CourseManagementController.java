package com.example.coursemanagement.controller;

import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseManagementController {

    @Autowired
    private CourseRepository courseRepository;

    // Get all Courses
    @GetMapping
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    // Get course by course code code
    @GetMapping("/{courseCode}")
    public List<Course> getCourse(@PathVariable String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }
}

