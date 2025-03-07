package com.example.coursemanagement.service.impl;

import java.util.List;

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
    public Course getCourse(String courseCode) {
        Course result = courseRepository.getCourse(courseCode);
		if(result != null) {
			return result;
		}else {
			throw new ResourceNotFoundException("Course", "courseCode", courseCode);
		}
    }

    @Override
    public Course getCourseById(int courseId) {
        Course result = courseRepository.getCourseById(courseId);
		if(result != null) {
			return result;
		}else {
			throw new ResourceNotFoundException("Course", "courseId", courseId + "");
		}
    }

    @Override
    public List<Course> searchCourse(String courseCode, String courseName) {
        return courseRepository.searchCourse(courseCode, courseName);

    }

    @Override
    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course editCourse(Course course) {
        Course existingCourse = courseRepository.getCourseById(course.getCourseId());
        if (existingCourse == null) {
            throw new ResourceNotFoundException("Course", "courseId", course.getCourseId().toString());
        }
        existingCourse.setCourseName(course.getCourseName());
        existingCourse.setCourseCode(course.getCourseCode());
        existingCourse.setRegistrationStart(course.getRegistrationStart());
        existingCourse.setRegistrationEnd(course.getRegistrationEnd());
        existingCourse.setMaxCapacity(course.getMaxCapacity());
        existingCourse.setStatus(course.getStatus());
        existingCourse.setCourseDesc(course.getCourseDesc());
        return courseRepository.save(existingCourse);
    }
    
}
