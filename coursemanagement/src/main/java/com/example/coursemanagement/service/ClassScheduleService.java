package com.example.coursemanagement.service;

import com.example.coursemanagement.model.ClassSchedule;

import java.time.LocalTime;
import java.util.List;


public interface ClassScheduleService {
    List<ClassSchedule> getAllClassSchedulesForCourse(int courseId);
    ClassSchedule getClassScheduleById(int classId);
    ClassSchedule addClassSchedule(ClassSchedule course);
    ClassSchedule editClassSchedule(ClassSchedule course);
    boolean existsByCourseAndDayOfWeekAndStartTimeAndEndTime(Integer courseId, String dayOfWeek, LocalTime startTime,
            LocalTime endTime);
} 
