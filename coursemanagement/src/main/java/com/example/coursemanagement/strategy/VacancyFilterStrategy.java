package com.example.coursemanagement.strategy;

import com.example.coursemanagement.model.ClassSchedule;

public interface VacancyFilterStrategy {
    boolean matches(ClassSchedule classSchedule);
    String getTitle();
}