package com.example.coursemanagement.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programId;

    private String programName;

    private String programDesc;

    @OneToMany
    @JoinTable(
        name = "program_course", 
        joinColumns = @JoinColumn(name = "program_id"), 
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    // Getters and Setters
    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
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

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
