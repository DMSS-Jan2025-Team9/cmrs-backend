package com.example.coursemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.coursemanagement.model.ProgramCourse;
import com.example.coursemanagement.model.ProgramCourseId;

public interface ProgramCourseRepository extends JpaRepository<ProgramCourse, ProgramCourseId> {
    
    List<ProgramCourse> findByCourseId(Integer courseId);
    
    List<ProgramCourse> findByProgramId(Integer programId);
    
    @Modifying
    @Query("DELETE FROM ProgramCourse pc WHERE pc.courseId = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);
    
    @Modifying
    @Query("DELETE FROM ProgramCourse pc WHERE pc.programId = :programId AND pc.courseId = :courseId")
    void deleteByProgramIdAndCourseId(@Param("programId") Integer programId, @Param("courseId") Integer courseId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProgramCourse pc WHERE pc.courseId = :courseId")
    void deleteAllByCourseId(@Param("courseId") int courseId);
}