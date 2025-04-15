package com.example.coursemanagement.dto;

import java.util.List;

public class ProgramDto {

    private Integer programId;
    private String programName;
    private String programDesc;
    private List<CourseDTO> courses;

    // Default constructor
    public ProgramDto() {
    }

    // All-args constructor
    public ProgramDto(Integer programId, String programName, String programDesc, List<CourseDTO> courses) {
        this.programId = programId;
        this.programName = programName;
        this.programDesc = programDesc;
        this.courses = courses;
    }

    // Getters and Setters
    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramDesc() {
        return programDesc;
    }

    public void setProgramDesc(String programDesc) {
        this.programDesc = programDesc;
    }

    public List<CourseDTO> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseDTO> courses) {
        this.courses = courses;
    }
}
