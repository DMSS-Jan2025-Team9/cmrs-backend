package com.example.coursemanagement.repository;
import com.example.coursemanagement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c WHERE c.courseCode = :courseCode")
    Course getCourse(String courseCode);

    @Query("SELECT c FROM Course c WHERE c.courseId = :courseId")
    Course getCourseById(int courseId);

    @Query("SELECT c FROM Course c WHERE c.courseCode LIKE %:courseCode% AND c.courseName LIKE %:courseName%")
    List<Course> searchCourse(String courseCode, String courseName);

}
