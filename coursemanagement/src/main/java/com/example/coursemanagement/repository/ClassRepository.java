package com.example.coursemanagement.repository;
import com.example.coursemanagement.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ClassRepository extends JpaRepository<Class, Long> {

    @Query("SELECT c FROM Class c WHERE " +
            "(:courseId IS NULL OR c.course.courseId = :courseId) AND " +
            "(:classId IS NULL OR c.classId = :classId) AND " +
            "(:maxCapacity IS NULL OR c.maxCapacity >= :maxCapacity) AND " +
            "(:dayOfWeek IS NULL OR c.dayOfWeek = :dayOfWeek)")
    List<Class> filterClasses(
            @Param("courseId") Long courseId,
            @Param("classId") Long classId,
            @Param("maxCapacity") Integer maxCapacity,
            @Param("dayOfWeek") String dayOfWeek
    );
}