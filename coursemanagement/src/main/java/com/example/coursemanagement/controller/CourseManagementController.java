package com.example.coursemanagement.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.dto.ErrorResponse;
import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.service.CourseService;


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

    @GetMapping("/getActiveCourses")
    public List<CourseDTO> getAllActiveCourses() {
         return courseService.findAllActiveCourses().stream().map(course -> modelMapper.map(course, CourseDTO.class))
            .toList();
    }

    // Get course by course code
    @GetMapping("/courseCode/{courseCode}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable String courseCode) {
        try {
            Course course = courseService.getCourseWithProgram(courseCode);
            CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
            
            // Set the programId in the DTO
            Integer programId = courseService.getProgramIdForCourse(course.getCourseId());
            courseDTO.setProgramId(programId);
            
            return ResponseEntity.ok().body(courseDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Get course by course id
    @GetMapping("/courseId/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Integer courseId) {
        try {
            Course course = courseService.getCourseByIdWithProgram(courseId);
            CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
            
            // Set the programId in the DTO
            Integer programId = courseService.getProgramIdForCourse(courseId);
            courseDTO.setProgramId(programId);
            
            return ResponseEntity.ok().body(courseDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/searchCourses")
    public List<CourseDTO> searchCourses(@RequestParam String courseCode, @RequestParam String courseName) {
        return courseService.searchCourse(courseCode, courseName).stream().map(course -> modelMapper.map(course, CourseDTO.class))
                .toList();
    }

    @PostMapping("/addCourse")
    public ResponseEntity<?> addCourse(@RequestBody CourseDTO courseDTO) {
        try {
            Course course = modelMapper.map(courseDTO, Course.class); // Map DTO to entity
            Course newCourse = courseService.addCourse(course, courseDTO.getProgramId()); // Save course
            CourseDTO newCourseDTO = modelMapper.map(newCourse, CourseDTO.class); // Map entity to DTO
            newCourseDTO.setProgramId(courseDTO.getProgramId()); // Set the programId in the response
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newCourseDTO); // Return DTO with status 201
        } catch (DuplicateIDException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Course with this code already exists", e.getMessage()));
        } catch (InvalidCapacityException | InvalidDateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Resource not found", e.getMessage()));            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", e.getMessage()));
        }
    }

    @PutMapping("/editCourse/{courseId}")
    public ResponseEntity<?> editCourse(@PathVariable int courseId, @RequestBody CourseDTO courseDTO) {
        try {
            Course existingCourse = courseService.getCourseById(courseId);
            if (existingCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            modelMapper.map(courseDTO, existingCourse); // Map updated fields to existing entity
            
            // Update course with program
            Course updatedCourse = courseService.editCourseWithProgram(existingCourse, courseDTO.getProgramId()); 
            
            CourseDTO updatedCourseDTO = modelMapper.map(updatedCourse, CourseDTO.class); // Map entity to DTO
            updatedCourseDTO.setProgramId(courseDTO.getProgramId()); // Set the programId in the response
            
            return ResponseEntity.ok().body(updatedCourseDTO); // Return updated DTO
        } catch (DuplicateIDException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Course with this code already exists", e.getMessage()));
        } catch (InvalidCapacityException | InvalidDateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Resource not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", e.getMessage()));
        }
    }

    @DeleteMapping("/deleteCourse/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable int courseId) {
        try {
            // Check if course exists before attempting to delete
            Course existingCourse = courseService.getCourseById(courseId);
            if (existingCourse == null) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Resource not found", "Course with id " + courseId + " not found"));
            }
            
            // Delete the course
            courseService.deleteCourse(courseId);
            
            // Return success response with no content
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Resource not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred", e.getMessage()));
        }
    }
}