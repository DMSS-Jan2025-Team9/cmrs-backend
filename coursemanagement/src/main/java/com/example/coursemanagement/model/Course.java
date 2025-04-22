package com.example.coursemanagement.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    private String courseName;
    
    @Column(unique = true)
    private String courseCode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationStart;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationEnd;

    private int maxCapacity;

    private String status;

    private String courseDesc;  
    

    public Course(){this.programs = new HashSet<>();
    } // Default constructor

    public Course(Integer courseId, String courseName, String courseCode, Date registrationStart, 
        Date registrationEnd, int maxCapacity, String status, String courseDesc) {
        this.programs = new HashSet<>();
    this.courseId = courseId;
    this.courseName = courseName;
    this.courseCode = courseCode;
    this.registrationStart = registrationStart;
    this.registrationEnd = registrationEnd;
    this.maxCapacity = maxCapacity;
    this.status = status;
    this.courseDesc = courseDesc;
    }


    // Getters and Setters
    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Date getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
    }

    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }

    @ManyToMany
    @JoinTable(
        name = "program_course",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "program_id")
    )
    private Set<Program> programs;
}
