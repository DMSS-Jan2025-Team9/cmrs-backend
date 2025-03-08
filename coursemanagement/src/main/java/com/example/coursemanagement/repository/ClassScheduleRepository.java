package com.example.coursemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.coursemanagement.model.ClassSchedule;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Integer>  {

    @Query("SELECT c FROM ClassSchedule c WHERE c.courseId = :courseId")
    List<ClassSchedule> getAllClassSchedulesForCourse(int courseId);


    @Query("SELECT c FROM ClassSchedule c WHERE c.classId = :classId")
    ClassSchedule getClassScheduleById(int classId);
    
}
