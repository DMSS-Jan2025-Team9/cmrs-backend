package com.example.coursemanagement.service;

import com.example.coursemanagement.model.Course;

import java.util.List;


public interface CourseService {

    List<Course> getAllCourses();
    Course getCourse(String courseCode);
    Course getCourseById(int courseId);
    List<Course> searchCourse(String courseCode, String courseName);
    Course addCourse(Course course);
    Course editCourse(Course course);
} 
