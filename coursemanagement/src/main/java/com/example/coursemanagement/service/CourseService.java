package com.example.coursemanagement.service;

import java.util.List;

import com.example.coursemanagement.model.Course;


public interface CourseService {

    List<Course> getAllCourses();
    List<Course> findAllActiveCourses();
    Course getCourse(String courseCode);
    Course getCourseWithProgram(String courseCode);
    Course getCourseById(int courseId);
    Course getCourseByIdWithProgram(int courseId);
    List<Course> searchCourse(String courseCode, String courseName);
    Course addCourse(Course course);
    Course editCourse(Course course);
    Course editCourseWithProgram(Course course, Integer programId);
    Course addCourse(Course course, Integer programId);
    Integer getProgramIdForCourse(Integer courseId);
} 
