package com.example.coursemanagement.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.coursemanagement.model.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    boolean existsByCourseCode(String courseCode);
    
    @Query("SELECT c FROM Course c WHERE c.courseCode = :courseCode")
    Course getCourse(String courseCode);

    @Query("SELECT c FROM Course c WHERE c.courseId = :courseId")
    Course getCourseById(int courseId);

    @Query("SELECT c FROM Course c WHERE c.courseCode LIKE %:courseCode% AND c.courseName LIKE %:courseName%")
    List<Course> searchCourse(String courseCode, String courseName);

    List<Course> findByStatus(String status);

}
