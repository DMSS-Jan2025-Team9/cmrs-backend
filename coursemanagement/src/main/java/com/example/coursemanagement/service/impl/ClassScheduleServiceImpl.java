package com.example.coursemanagement.service.impl;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.ClassSchedule;

import com.example.coursemanagement.repository.ClassScheduleRepository;
import com.example.coursemanagement.service.ClassScheduleService;

@Service
public class ClassScheduleServiceImpl implements ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;
    private static final String CLASSSCHEDULE = "Class Schedule";

    public ClassScheduleServiceImpl(ClassScheduleRepository classScheduleRepository) {
        super();
        this.classScheduleRepository = classScheduleRepository;
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

        return classScheduleRepository.save(classSchedule);
    }


    @Override
    public ClassSchedule editClassSchedule(ClassSchedule classSchedule) {
      
        ClassSchedule existingClass = classScheduleRepository.getClassScheduleById(classSchedule.getClassId());
        if (existingClass == null) {
            throw new ResourceNotFoundException(CLASSSCHEDULE);
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

        existingClass.setCourse(classSchedule.getCourse());
        existingClass.setDayOfWeek(classSchedule.getDayOfWeek());
        existingClass.setStartTime(classSchedule.getStartTime());
        existingClass.setEndTime(classSchedule.getEndTime());
        existingClass.setMaxCapacity(classSchedule.getMaxCapacity());
        existingClass.setVacancy(classSchedule.getVacancy());

        return classScheduleRepository.save(existingClass);
    }

	@Override
	public boolean existsByCourseAndDayOfWeekAndStartTimeAndEndTime(Integer courseId, String dayOfWeek,
			LocalTime startTime, LocalTime endTime) {
        return classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
            courseId, dayOfWeek, startTime, endTime);
	}


}
