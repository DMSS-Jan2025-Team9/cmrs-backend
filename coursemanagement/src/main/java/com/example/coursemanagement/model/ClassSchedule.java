// Class Entity
package com.example.coursemanagement.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "class") // Explicitly specify table name as "class" is a Java keyword
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer classId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String dayOfWeek;

    private LocalTime startTime;
    
    private LocalTime endTime;

    private int maxCapacity;

    private int vacancy;

    // Default constructor
    public ClassSchedule() {
    }

    // Constructor with all fields
    public ClassSchedule(Integer classId, Course course, String dayOfWeek, LocalTime startTime,
                        LocalTime endTime, int maxCapacity, int vacancy) {
        this.classId = classId;
        this.course = course;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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