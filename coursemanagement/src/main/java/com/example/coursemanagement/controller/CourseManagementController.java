package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.service.CourseService;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/courses")
public class CourseManagementController {

    private final ModelMapper modelMapper;

    private final CourseService courseService;

    public CourseManagementController(CourseService courseService, ModelMapper modelMapper) {
        super();
        this.courseService = courseService;
        this.modelMapper = modelMapper;
    }

    // Get all Courses
    @GetMapping
    public List<CourseDTO> getAllCourses() {
        return courseService.getAllCourses().stream().map(course -> modelMapper.map(course, CourseDTO.class))
            .toList();
    }

    // Get course by course code code
    @GetMapping("/courseCode/{courseCode}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable String courseCode) {
        Course course = courseService.getCourse(courseCode);
		CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
        return ResponseEntity.ok().body(courseDTO);
    }

    // Get course by course code code
    @GetMapping("/courseId/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Integer courseId) {
        Course course = courseService.getCourseById(courseId);
        CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
        return ResponseEntity.ok().body(courseDTO);
    }

    @GetMapping("/searchCourses")
    public List<CourseDTO> searchCourses(@RequestParam String courseCode, @RequestParam String courseName) {
        return courseService.searchCourse(courseCode, courseName).stream().map(course -> modelMapper.map(course, CourseDTO.class))
                .toList();
    }

    @PostMapping("/addCourse")
    public ResponseEntity<CourseDTO> addCourse(@RequestBody CourseDTO courseDTO) {
        Course course = modelMapper.map(courseDTO, Course.class); // Map DTO to entity
        Course newCourse = courseService.addCourse(course); // Save course
        CourseDTO newCourseDTO = modelMapper.map(newCourse, CourseDTO.class); // Map entity to DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourseDTO); // Return DTO with status 201
    }

    @PutMapping("/editCourse/{courseId}")
    public ResponseEntity<CourseDTO> editCourse(@PathVariable int courseId, @RequestBody CourseDTO courseDTO) {
        Course existingCourse = courseService.getCourseById(courseId);
        if (existingCourse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        modelMapper.map(courseDTO, existingCourse); // Map updated fields to existing entity
        Course updatedCourse = courseService.editCourse(existingCourse); // Update course
        CourseDTO updatedCourseDTO = modelMapper.map(updatedCourse, CourseDTO.class); // Map entity to DTO
        return ResponseEntity.ok().body(updatedCourseDTO); // Return updated DTO
    }
    
}

