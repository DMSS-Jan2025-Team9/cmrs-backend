package com.example.courseregistration.dto;
import java.util.List;
import java.time.LocalTime;

public class CourseClassDTO {
        private Long classId;
        private Long courseId;
        private String dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private int maxCapacity;
        private int vacancy;

        public CourseClassDTO() {
        }

        public Long getClassId() {
            return classId;
        }

        public void setClassId(Long classId) {
            this.classId = classId;
        }

        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
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