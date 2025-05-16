package com.example.coursemanagement.strategy.impl;

import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.strategy.VacancyFilterStrategy;

public class FullClassesStrategy implements VacancyFilterStrategy {
    @Override
    public boolean matches(ClassSchedule classSchedule) {
        return classSchedule.getVacancy() == 0;
    }
    
    @Override
    public String getTitle() {
        return "Full Classes";
    }
}


