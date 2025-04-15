package com.example.coursemanagement.dto;

import java.util.Date;

public class CourseDTO {

    private Integer courseId;
    private String courseName;
    private String courseCode;
    private Date registrationStart;
    private Date registrationEnd;
    private int maxCapacity;
    private String status;
    private String courseDesc;
    private Integer programId;
    

    // Constructor
    public CourseDTO() {
    }

  public CourseDTO(Integer courseId, String courseName, String courseCode, Date registrationStart, 
                     Date registrationEnd, int maxCapacity, String status, String courseDesc) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.maxCapacity = maxCapacity;
        this.status = status;
        this.courseDesc = courseDesc;
    }

    public CourseDTO(Integer courseId, String courseName, String courseCode, Date registrationStart, 
                     Date registrationEnd, int maxCapacity, String status, String courseDesc, Integer programId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.maxCapacity = maxCapacity;
        this.status = status;
        this.courseDesc = courseDesc;
        this.programId = programId;
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

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

}
