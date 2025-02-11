package com.example.coursemanagement.repository;
import com.example.coursemanagement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCourseCode(String courseCode);
}
