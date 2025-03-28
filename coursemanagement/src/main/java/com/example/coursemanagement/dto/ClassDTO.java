package com.example.coursemanagement.dto;

import java.time.LocalTime;

public class ClassDTO {
    private Long classId;
    private Integer courseId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxCapacity;
    private int vacancy;

    public ClassDTO(Long classId, Integer courseId, String dayOfWeek, LocalTime startTime, LocalTime endTime, int maxCapacity, int vacancy) {
        this.classId = classId;
        this.courseId = courseId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.vacancy = vacancy;
    }

    public Long getClassId() {
        return classId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getVacancy() {
        return vacancy;
    }
}