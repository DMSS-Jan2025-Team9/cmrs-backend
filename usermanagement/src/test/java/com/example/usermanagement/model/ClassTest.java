package com.example.usermanagement.model;

import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassTest {

    @Test
    public void testClassGettersAndSetters() {
        // Arrange
        Class classObj = new Class();
        Long classId = 1L;
        Course course = new Course();
        String dayOfWeek = "Monday";
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        int maxCapacity = 30;
        int vacancy = 15;

        // Act
        classObj.setClassId(classId);
        classObj.setCourse(course);
        classObj.setDayOfWeek(dayOfWeek);
        classObj.setStartTime(startTime);
        classObj.setEndTime(endTime);
        classObj.setMaxCapacity(maxCapacity);
        classObj.setVacancy(vacancy);

        // Assert
        assertEquals(classId, classObj.getClassId());
        assertEquals(course, classObj.getCourse());
        assertEquals(dayOfWeek, classObj.getDayOfWeek());
        assertEquals(startTime, classObj.getStartTime());
        assertEquals(endTime, classObj.getEndTime());
        assertEquals(maxCapacity, classObj.getMaxCapacity());
        assertEquals(vacancy, classObj.getVacancy());
    }
}