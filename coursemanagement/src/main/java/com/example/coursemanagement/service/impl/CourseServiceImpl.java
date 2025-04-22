package com.example.coursemanagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.model.Program;
import com.example.coursemanagement.model.ProgramCourse;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.ProgramCourseRepository;
import com.example.coursemanagement.repository.ProgramRepository;
import com.example.coursemanagement.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;
    private final ProgramCourseRepository programCourseRepository;
    private static final String COURSE = "course";
    private static final String PROGRAM = "program";
	
    public CourseServiceImpl(CourseRepository courseRepository, 
                            ProgramRepository programRepository,
                            ProgramCourseRepository programCourseRepository) {
        super();
        this.courseRepository = courseRepository;
        this.programRepository = programRepository;
        this.programCourseRepository = programCourseRepository;
    }
    
    @Override
    public List<Course> getAllCourses() {
       return courseRepository.findAll();
    }

    public List<Course> findAllActiveCourses() {
        return courseRepository.findByStatus("active");
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
    public Course getCourseWithProgram(String courseCode) {
        Course course = getCourse(courseCode);
        // The courses are already populated with programs due to JPA relationship
        return course;
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
    public Course getCourseByIdWithProgram(int courseId) {
        Course course = getCourseById(courseId);
        // The courses are already populated with programs due to JPA relationship
        return course;
    }

    @Override
    public List<Course> searchCourse(String courseCode, String courseName) {
        return courseRepository.searchCourse(courseCode, courseName);
    }

    @Override
    public Course addCourse(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new DuplicateIDException("Course code already exists");
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
        if (!existingCourse.getCourseCode().equals(course.getCourseCode()) && 
            courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new DuplicateIDException("Course code already exists");
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
    
    @Override
    @Transactional
    public Course editCourseWithProgram(Course course, Integer programId) {
        Course updatedCourse = editCourse(course);
        
        // Check if program exists
        Program program = programRepository.findById(programId)
            .orElseThrow(() -> new ResourceNotFoundException(PROGRAM, "programId", programId.toString()));
        
        // Update program association if programId has changed
        // First, check if there's an existing association
        List<ProgramCourse> existingAssociations = programCourseRepository.findByCourseId(updatedCourse.getCourseId());
        
        if (!existingAssociations.isEmpty()) {
            // If program has changed, update it
            boolean programFound = false;
            for (ProgramCourse pc : existingAssociations) {
                if (pc.getProgramId().equals(programId)) {
                    programFound = true;
                    break;
                }
            }
            
            if (!programFound) {
                // Remove existing associations
                programCourseRepository.deleteByCourseId(updatedCourse.getCourseId());
                
                // Create new association
                ProgramCourse programCourse = new ProgramCourse();
                programCourse.setProgramId(programId);
                programCourse.setCourseId(updatedCourse.getCourseId());
                programCourseRepository.save(programCourse);
            }
        } else {
            // Create new association if none exists
            ProgramCourse programCourse = new ProgramCourse();
            programCourse.setProgramId(programId);
            programCourse.setCourseId(updatedCourse.getCourseId());
            programCourseRepository.save(programCourse);
        }
        
        return updatedCourse;
    }

    @Override
    @Transactional
    public Course addCourse(Course course, Integer programId) {
        // Validate the course first (reusing your existing validation)
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new DuplicateIDException("Course code already exists");
        }
        
        if (course.getMaxCapacity() <= 0) {
            throw new InvalidCapacityException("Capacity must be a positive number");
        }
        
        if (course.getRegistrationStart() != null && course.getRegistrationEnd() != null &&
                course.getRegistrationStart().after(course.getRegistrationEnd())) {
            throw new InvalidDateException("Start date cannot be after end date");
        }
        
        // Check if program exists
        Program program = programRepository.findById(programId)
            .orElseThrow(() -> new ResourceNotFoundException(PROGRAM, "programId", programId.toString()));
        
        // Save the course first to get the course ID
        Course savedCourse = courseRepository.save(course);
        
        // Create the program_course association
        ProgramCourse programCourse = new ProgramCourse();
        programCourse.setProgramId(programId);
        programCourse.setCourseId(savedCourse.getCourseId());
        programCourseRepository.save(programCourse);
        
        return savedCourse;
    }
    
    @Override
    public Integer getProgramIdForCourse(Integer courseId) {
        List<ProgramCourse> programCourses = programCourseRepository.findByCourseId(courseId);
        if (programCourses.isEmpty()) {
            throw new ResourceNotFoundException(COURSE, "courseId with program association", courseId.toString());
        }
        return programCourses.get(0).getProgramId();
    }
}