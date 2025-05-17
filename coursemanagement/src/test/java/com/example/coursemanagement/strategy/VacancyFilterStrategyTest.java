package com.example.coursemanagement.strategy;

import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.strategy.impl.FullClassesStrategy;
import com.example.coursemanagement.strategy.impl.MostlyEmptyClassesStrategy;
import com.example.coursemanagement.strategy.impl.NearFullClassesStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class VacancyFilterStrategyTest {

    private ClassSchedule fullClass;
    private ClassSchedule nearFullClass;
    private ClassSchedule halfFullClass;
    private ClassSchedule mostlyEmptyClass;

    @BeforeEach
    public void setup() {
        Course course = new Course();
        course.setCourseId(1);
        course.setCourseCode("CS101");

        // Full class (0% vacancy)
        fullClass = new ClassSchedule();
        fullClass.setClassId(1);
        fullClass.setCourse(course);
        fullClass.setMaxCapacity(30);
        fullClass.setVacancy(0);

        // Near full class (10% vacancy)
        nearFullClass = new ClassSchedule();
        nearFullClass.setClassId(2);
        nearFullClass.setCourse(course);
        nearFullClass.setMaxCapacity(30);
        nearFullClass.setVacancy(3);

        // Half full class (50% vacancy)
        halfFullClass = new ClassSchedule();
        halfFullClass.setClassId(3);
        halfFullClass.setCourse(course);
        halfFullClass.setMaxCapacity(30);
        halfFullClass.setVacancy(15);

        // Mostly empty class (80% vacancy)
        mostlyEmptyClass = new ClassSchedule();
        mostlyEmptyClass.setClassId(4);
        mostlyEmptyClass.setCourse(course);
        mostlyEmptyClass.setMaxCapacity(30);
        mostlyEmptyClass.setVacancy(24);
    }

    @Test
    public void testFullClassesStrategy() {
        VacancyFilterStrategy strategy = new FullClassesStrategy();

        // Verify strategy name
        assertEquals("Full Classes", strategy.getTitle());

        // Verify matches
        assertTrue(strategy.matches(fullClass));
        assertFalse(strategy.matches(nearFullClass));
        assertFalse(strategy.matches(halfFullClass));
        assertFalse(strategy.matches(mostlyEmptyClass));
    }

    @Test
    public void testNearFullClassesStrategy() {
        VacancyFilterStrategy strategy = new NearFullClassesStrategy();

        // Verify strategy name
        assertEquals("Nearly Full Classes", strategy.getTitle());

        // Verify matches
        assertFalse(strategy.matches(fullClass)); // Full classes are NOT near full
        assertTrue(strategy.matches(nearFullClass));
        assertFalse(strategy.matches(halfFullClass));
        assertFalse(strategy.matches(mostlyEmptyClass));
    }

    @Test
    public void testMostlyEmptyClassesStrategy() {
        VacancyFilterStrategy strategy = new MostlyEmptyClassesStrategy();

        // Verify strategy name
        assertEquals("Low Enrollment Classes", strategy.getTitle());

        // Verify matches
        assertFalse(strategy.matches(fullClass));
        assertFalse(strategy.matches(nearFullClass));
        assertFalse(strategy.matches(halfFullClass));
        assertTrue(strategy.matches(mostlyEmptyClass));
    }

    @Test
    public void testEdgeCases() {
        // Create strategies
        VacancyFilterStrategy fullStrategy = new FullClassesStrategy();
        VacancyFilterStrategy nearFullStrategy = new NearFullClassesStrategy();
        VacancyFilterStrategy mostlyEmptyStrategy = new MostlyEmptyClassesStrategy();

        // Test with a class that has exact 20% vacancy (edge of near full)
        ClassSchedule exactlyTwentyPercentVacancy = new ClassSchedule();
        exactlyTwentyPercentVacancy.setMaxCapacity(100);
        exactlyTwentyPercentVacancy.setVacancy(20);

        assertFalse(fullStrategy.matches(exactlyTwentyPercentVacancy));
        assertTrue(nearFullStrategy.matches(exactlyTwentyPercentVacancy)); // <= 20%, not <
        assertFalse(mostlyEmptyStrategy.matches(exactlyTwentyPercentVacancy));

        // Test with a class that has exact 80% vacancy (edge of mostly empty)
        ClassSchedule exactlyEightyPercentVacancy = new ClassSchedule();
        exactlyEightyPercentVacancy.setMaxCapacity(100);
        exactlyEightyPercentVacancy.setVacancy(80);

        assertFalse(fullStrategy.matches(exactlyEightyPercentVacancy));
        assertFalse(nearFullStrategy.matches(exactlyEightyPercentVacancy));
        assertTrue(mostlyEmptyStrategy.matches(exactlyEightyPercentVacancy)); // >= 80%, not >
    }
}