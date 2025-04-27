package com.example.coursemanagement.model;

import java.io.Serializable;
import java.util.Objects;

public class ProgramCourseId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer programId;
    private Integer courseId;
    
    // Default no-args constructor (required by JPA)
    public ProgramCourseId() {
    }
    
    // Constructor with all fields
    public ProgramCourseId(Integer programId, Integer courseId) {
        this.programId = programId;
        this.courseId = courseId;
    }
    
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
    
    // equals and hashCode methods are crucial for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramCourseId that = (ProgramCourseId) o;
        return Objects.equals(programId, that.programId) && 
               Objects.equals(courseId, that.courseId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(programId, courseId);
    }
    
    @Override
    public String toString() {
        return "ProgramCourseId{" +
                "programId=" + programId +
                ", courseId=" + courseId +
                '}';
    }
}