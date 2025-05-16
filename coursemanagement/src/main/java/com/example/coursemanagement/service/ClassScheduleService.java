package com.example.coursemanagement.service;

import java.time.LocalTime;
import java.util.List;

import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.strategy.VacancyFilterStrategy;


public interface ClassScheduleService {
    List<ClassSchedule> getAllClassSchedulesForCourse(int courseId);
    ClassSchedule getClassScheduleById(int classId);
    ClassSchedule addClassSchedule(ClassSchedule course);
    ClassSchedule editClassSchedule(ClassSchedule course);
    boolean deleteClassSchedule(int classId);
    
    boolean existsByCourseAndDayOfWeekAndStartTimeAndEndTime(Integer courseId, String dayOfWeek, LocalTime startTime,
            LocalTime endTime);

    List<ClassSchedule> getClassesByVacancyFilter(VacancyFilterStrategy strategy);
    // List<ClassSchedule> getFullClasses();
    // List<ClassSchedule> getNearFullClasses(); 
    // List<ClassSchedule> getMostlyEmptyClasses();
} 
