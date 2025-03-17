package com.example.usermanagement.dto;

import java.util.List;

public class ProgramResponse {
    private Long programId;
    private String programName;
    private String programDesc;
    private List<Course> courses; // You can create a Course DTO if needed

    // Getters and setters
    public Long getProgramId() { return programId; }
    public void setProgramId(Long programId) { this.programId = programId; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public String getProgramDesc() { return programDesc; }
    public void setProgramDesc(String programDesc) { this.programDesc = programDesc; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}

