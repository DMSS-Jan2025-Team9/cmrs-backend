package com.example.coursemanagement.dto;

import java.time.LocalTime;

public class ClassScheduleDTO {

    private Integer classId;
    private Integer courseId;  
    private String courseName; 
    private String courseCode; 
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxCapacity;
    private int vacancy;

    // Default constructor
    public ClassScheduleDTO() {
    }

    // Constructor with all fields
    public ClassScheduleDTO(Integer classId, Integer courseId, String courseName, String courseCode,
                           String dayOfWeek, LocalTime startTime, LocalTime endTime, 
                           int maxCapacity, int vacancy) {
        this.classId = classId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.vacancy = vacancy;
    }

    // Constructor without optional fields
    public ClassScheduleDTO(Integer classId, Integer courseId, String dayOfWeek, 
                           LocalTime startTime, LocalTime endTime, int maxCapacity, int vacancy) {
        this.classId = classId;
        this.courseId = courseId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.vacancy = vacancy;
    }

    // Getters and Setters
    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getVacancy() {
        return vacancy;
    }

    public void setVacancy(int vacancy) {
        this.vacancy = vacancy;
    }
}