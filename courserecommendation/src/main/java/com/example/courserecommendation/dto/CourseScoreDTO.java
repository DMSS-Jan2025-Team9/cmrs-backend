package com.example.courserecommendation.dto;

public class CourseScoreDTO {
    private Integer courseId;
    private String courseName;
    private double score;

    public CourseScoreDTO() {}

    public CourseScoreDTO(Integer courseId, String courseName, double score) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.score = score;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}