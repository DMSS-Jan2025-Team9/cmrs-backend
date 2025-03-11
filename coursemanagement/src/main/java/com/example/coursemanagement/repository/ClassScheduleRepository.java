package com.example.coursemanagement.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.coursemanagement.model.ClassSchedule;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Integer>  {

    @Query("SELECT c FROM ClassSchedule c WHERE c.course.courseId = :courseId")
    List<ClassSchedule> getAllClassSchedulesForCourse(int courseId);


    @Query("SELECT c FROM ClassSchedule c WHERE c.classId = :classId")
    ClassSchedule getClassScheduleById(int classId);

    boolean existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(Integer courseId, String dayOfWeek, 
        LocalTime startTime, LocalTime endTime);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
       "FROM ClassSchedule c " +
       "WHERE c.course.courseId = :courseId " +
       "AND c.dayOfWeek = :dayOfWeek " +
       "AND c.startTime = :startTime " +
       "AND c.endTime = :endTime " +
       "AND c.classId != :classId")
    boolean existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTimeAndClassIdNot(
        @Param("courseId") Integer courseId,
        @Param("dayOfWeek") String dayOfWeek,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime,
        @Param("classId") Integer classId);
    
    
}
