package com.example.coursemanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "program_course")
@IdClass(ProgramCourseId.class)
public class ProgramCourse {
    
    @Id
    @Column(name = "program_id")
    private Integer programId;
    
    @Id
    @Column(name = "course_id")
    private Integer courseId;
    
    // Getters and setters
    public Integer getProgramId() {
        return programId;
    }
    
    public void setProgramId(Integer programId) {
        this.programId = programId;
    }
    
    public Integer getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
}