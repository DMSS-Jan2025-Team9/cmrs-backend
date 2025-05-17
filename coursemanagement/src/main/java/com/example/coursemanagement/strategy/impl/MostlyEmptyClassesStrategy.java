package com.example.coursemanagement.strategy.impl;

import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.strategy.VacancyFilterStrategy;

public class MostlyEmptyClassesStrategy implements VacancyFilterStrategy {
    @Override
    public boolean matches(ClassSchedule classSchedule) {
        int maxCapacity = classSchedule.getMaxCapacity();
        int vacancy = classSchedule.getVacancy();
        double vacancyPercentage = maxCapacity > 0 ? 
                ((double) vacancy / maxCapacity) * 100 : 0;
        return vacancyPercentage >= 80.0;
    }
    
    @Override
    public String getTitle() {
        return "Low Enrollment Classes";
    }
}