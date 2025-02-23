package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.service.CourseService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseManagementController {

	@Autowired
	private ModelMapper modelMapper;

	private CourseService courseService;

	public CourseManagementController(CourseService courseService) {
		super();
		this.courseService = courseService;
	}

    // Get all Courses
    @GetMapping
    public List<CourseDTO> getAllCourses() {
        return courseService.getAllCourses().stream().map(course -> modelMapper.map(course, CourseDTO.class))
				.collect(Collectors.toList());
    }

    // Get course by course code code
    @GetMapping("/{courseCode}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable String courseCode) {
        Course course = courseService.getCourse(courseCode);
		CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
        return ResponseEntity.ok().body(courseDTO);
    }
}

