package com.example.coursemanagement.service.impl;

import java.util.List;

import com.example.coursemanagement.exception.DuplicateCourseCodeException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.service.CourseService;

import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;
    private static final String COURSE = "course";
	
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
			throw new ResourceNotFoundException(COURSE, "courseCode", courseCode);
		}
    }

    @Override
    public Course getCourseById(int courseId) {
        Course result = courseRepository.getCourseById(courseId);
		if(result != null) {
			return result;
		}else {
			throw new ResourceNotFoundException(COURSE, "courseId", courseId + "");
		}
    }

    @Override
    public List<Course> searchCourse(String courseCode, String courseName) {
        return courseRepository.searchCourse(courseCode, courseName);

    }

    @Override
    public Course addCourse(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new DuplicateCourseCodeException("Course code already exists");
        }
        
        if (course.getMaxCapacity() <= 0) {
            throw new InvalidCapacityException("Capacity must be a positive number");
        }
        
        if (course.getRegistrationStart() != null && course.getRegistrationEnd() != null &&
                course.getRegistrationStart().after(course.getRegistrationEnd())) {
            throw new InvalidDateException("Start date cannot be after end date");
        }
        return courseRepository.save(course);
    }

    @Override
    public Course editCourse(Course course) {
        Course existingCourse = courseRepository.getCourseById(course.getCourseId());
        if (existingCourse == null) {
            throw new ResourceNotFoundException(COURSE, "courseId", course.getCourseId().toString());
        }
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new DuplicateCourseCodeException("Course code already exists");
        }
        
        if (course.getMaxCapacity() <= 0) {
            throw new InvalidCapacityException("Capacity must be a positive number");
        }
        
        if (course.getRegistrationStart() != null && course.getRegistrationEnd() != null &&
                course.getRegistrationStart().after(course.getRegistrationEnd())) {
            throw new InvalidDateException("Start date cannot be after end date");
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
