package com.example.coursemanagement.service.impl;

import java.util.List;

import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.repository.ClassScheduleRepository;
import com.example.coursemanagement.service.ClassScheduleService;

public class ClassScheduleServiceImpl implements ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;

    public ClassScheduleServiceImpl(ClassScheduleRepository classScheduleRepository) {
        super();
        this.classScheduleRepository = classScheduleRepository;
    }

    @Override
    public List<ClassSchedule> getAllClassSchedulesForCourse(int courseId) {
        return classScheduleRepository.getAllClassSchedulesForCourse(courseId);
    }

    @Override
    public ClassSchedule getClassScheduleById(int classId) {
        ClassSchedule result = classScheduleRepository.getClassScheduleById(classId);
		if(result != null) {
			return result;
		}else {
			throw new ResourceNotFoundException("Class", "classId", classId + "");
		}
    }

    @Override
    public ClassSchedule addClassSchedule(ClassSchedule course) {
        return classScheduleRepository.save(course);
    }


    @Override
    public ClassSchedule editClassSchedule(ClassSchedule classSchedule) {
      
        ClassSchedule existingClass = classScheduleRepository.getClassScheduleById(classSchedule.getClassId());
        if (existingClass == null) {
            throw new ResourceNotFoundException("Class Schedule", "classId", classSchedule.getClassId().toString());
        }

        existingClass.setCourse(classSchedule.getCourse());
        existingClass.setDayOfWeek(classSchedule.getDayOfWeek());
        existingClass.setStartTime(classSchedule.getStartTime());
        existingClass.setEndTime(classSchedule.getEndTime());
        existingClass.setMaxCapacity(classSchedule.getMaxCapacity());
        existingClass.setVacancy(classSchedule.getVacancy());

        return classScheduleRepository.save(existingClass);
    }
}
