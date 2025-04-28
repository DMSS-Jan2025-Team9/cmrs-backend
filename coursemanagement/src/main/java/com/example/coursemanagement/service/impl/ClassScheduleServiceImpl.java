package com.example.coursemanagement.service.impl;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.repository.ClassScheduleRepository;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.service.ClassScheduleService;

@Service
public class ClassScheduleServiceImpl implements ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;
    private final CourseRepository courseRepository;
    private static final String CLASSSCHEDULE = "Class Schedule";

    public ClassScheduleServiceImpl(ClassScheduleRepository classScheduleRepository, CourseRepository courseRepository) {
        super();
        this.classScheduleRepository = classScheduleRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<ClassSchedule> getAllClassSchedulesForCourse(int courseId) {
        List<ClassSchedule> result = classScheduleRepository.getAllClassSchedulesForCourse(courseId);
        if(result != null) {
            return result;
        }else {
            throw new ResourceNotFoundException("Course", "courseId", courseId + "");
        }
    }

    @Override
    public ClassSchedule getClassScheduleById(int classId) {
        ClassSchedule result = classScheduleRepository.getClassScheduleById(classId);
		if(result != null) {
			return result;
		}else {
			throw new ResourceNotFoundException(CLASSSCHEDULE, "classId", classId + "");
		}
    }

    @Override
    public ClassSchedule addClassSchedule(ClassSchedule classSchedule) {
        if(classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(classSchedule.getCourse().getCourseId(), classSchedule.getDayOfWeek(), classSchedule.getStartTime(), classSchedule.getEndTime())) {
            throw new ResourceNotFoundException(CLASSSCHEDULE, "course, dayOfWeek, startTime, endTime", classSchedule.toString());
        }
        if(classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(classSchedule.getCourse().getCourseId(), classSchedule.getDayOfWeek(), classSchedule.getStartTime(), classSchedule.getEndTime())) {
            throw new DuplicateIDException(classSchedule.toString());
        }
        if(classSchedule.getVacancy() > classSchedule.getMaxCapacity()) {
            throw new InvalidCapacityException("Vacancy cannot be more than max capacity");
        }

        if(classSchedule.getStartTime().isAfter(classSchedule.getEndTime())) {
            throw new InvalidDateException("Start date cannot be after end date");
        }
        
        // Check course max capacity constraint
        validateCourseCapacity(classSchedule, 0);

        return classScheduleRepository.save(classSchedule);
    }


    @Override
    public ClassSchedule editClassSchedule(ClassSchedule classSchedule) {
      
        ClassSchedule existingClass = classScheduleRepository.getClassScheduleById(classSchedule.getClassId());
        if (existingClass == null) {
            throw new ResourceNotFoundException(CLASSSCHEDULE);
        }
        if (classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTimeAndClassIdNot(
            classSchedule.getCourse().getCourseId(), 
            classSchedule.getDayOfWeek(), 
            classSchedule.getStartTime(), 
            classSchedule.getEndTime(), 
            classSchedule.getClassId())) {
            throw new DuplicateIDException(classSchedule.toString());
        }

        if(classSchedule.getVacancy() > classSchedule.getMaxCapacity()) {
            throw new InvalidCapacityException("Vacancy cannot be more than max capacity");
        }

        if(classSchedule.getStartTime().isAfter(classSchedule.getEndTime())) {
            throw new InvalidDateException("Start date cannot be after end date");
        }
        
        // Check course max capacity constraint
        validateCourseCapacity(classSchedule, existingClass.getMaxCapacity());

        existingClass.setCourse(classSchedule.getCourse());
        existingClass.setDayOfWeek(classSchedule.getDayOfWeek());
        existingClass.setStartTime(classSchedule.getStartTime());
        existingClass.setEndTime(classSchedule.getEndTime());
        existingClass.setMaxCapacity(classSchedule.getMaxCapacity());
        existingClass.setVacancy(classSchedule.getVacancy());

        return classScheduleRepository.save(existingClass);
    }

    @Override
    public boolean deleteClassSchedule(int classId) {
        ClassSchedule classSchedule = classScheduleRepository.getClassScheduleById(classId);
        if (classSchedule == null) {
            throw new ResourceNotFoundException(CLASSSCHEDULE, "classId", classId + "");
        }
        
        classScheduleRepository.delete(classSchedule);
        return true;
    }

    /**
     * Validates that the total capacity of all classes for a course doesn't exceed 
     * the course's maximum capacity.
     * 
     * @param classSchedule The class schedule being added or edited
     * @param existingCapacity For edits, the existing capacity to subtract from total
     * @throws InvalidCapacityException If the total capacity exceeds course maximum
     */
    private void validateCourseCapacity(ClassSchedule classSchedule, int existingCapacity) {
        int courseId = classSchedule.getCourse().getCourseId();
        
        // Get the course to check its max capacity
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course", "courseId", courseId + ""));
        
        int courseMaxCapacity = course.getMaxCapacity();
        
        // Get all existing class schedules for this course
        List<ClassSchedule> existingClasses = classScheduleRepository.getAllClassSchedulesForCourse(courseId);
        
        // Calculate total capacity of existing classes
        int totalExistingCapacity = existingClasses.stream()
            .mapToInt(ClassSchedule::getMaxCapacity)
            .sum();
        
        // For edits, subtract the existing class capacity that's being modified
        totalExistingCapacity -= existingCapacity;
        
        // Add the new capacity
        int newTotalCapacity = totalExistingCapacity + classSchedule.getMaxCapacity();
        
        // Check if exceeds course max capacity
        if (newTotalCapacity > courseMaxCapacity) {
            throw new InvalidCapacityException("Total capacity of all classes (" + newTotalCapacity + 
                ") exceeds course maximum capacity (" + courseMaxCapacity + ")");
        }
    }

	@Override
	public boolean existsByCourseAndDayOfWeekAndStartTimeAndEndTime(Integer courseId, String dayOfWeek,
			LocalTime startTime, LocalTime endTime) {
        return classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
            courseId, dayOfWeek, startTime, endTime);
	}

    @Override
    public List<ClassSchedule> getFullClasses() {
        // Get all class schedules from repository
        List<ClassSchedule> allClassSchedules = classScheduleRepository.findAll();
        
        // Filter classes with 0 vacancy
        return allClassSchedules.stream()
                .filter(classSchedule -> classSchedule.getVacancy() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassSchedule> getNearFullClasses() {
        // Get all class schedules from repository
        List<ClassSchedule> allClassSchedules = classScheduleRepository.findAll();
        
        // Filter classes with 20% or less vacancy
        return allClassSchedules.stream()
                .filter(classSchedule -> {
                    int maxCapacity = classSchedule.getMaxCapacity();
                    int vacancy = classSchedule.getVacancy();
                    // Calculate vacancy percentage
                    double vacancyPercentage = maxCapacity > 0 ? 
                            ((double) vacancy / maxCapacity) * 100 : 0;
                    // Return true if vacancy percentage is 20% or less
                    return vacancyPercentage <= 20.0 && vacancy > 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassSchedule> getMostlyEmptyClasses() {
        // Get all class schedules from repository
        List<ClassSchedule> allClassSchedules = classScheduleRepository.findAll();
        
        // Filter classes with 80% or more vacancy
        return allClassSchedules.stream()
                .filter(classSchedule -> {
                    int maxCapacity = classSchedule.getMaxCapacity();
                    int vacancy = classSchedule.getVacancy();
                    // Calculate vacancy percentage
                    double vacancyPercentage = maxCapacity > 0 ? 
                            ((double) vacancy / maxCapacity) * 100 : 0;
                    // Return true if vacancy percentage is 80% or more
                    return vacancyPercentage >= 80.0;
                })
                .collect(Collectors.toList());
    }

}