package com.example.coursemanagement.service;

import com.example.coursemanagement.model.Course;

import java.util.List;


public interface CourseService {

    List<Course> getAllCourses();
    Course getCourse(String courseCode);
} 
