package com.example.coursemanagement.service.impl;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.service.CourseService;

import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;
	
	public CourseServiceImpl(CourseRepository courseRepository) {
		super();
		this.courseRepository = courseRepository;
	}

    @Override
    public List<Course> getAllCourses() {
       return courseRepository.findAll();
    }

    @Override
    public Course getCourse(@PathVariable String courseCode) {
        Course result = courseRepository.getCourse(courseCode);
		if(result != null) {
			return result;
		}else {
			throw new ResourceNotFoundException("Course", "courseCode", courseCode);
		}
    }

    @Override
    public List<Course> searchCourse(String courseCode, String courseName) {
        return courseRepository.searchCourse(courseCode, courseName);

    }
    
}
