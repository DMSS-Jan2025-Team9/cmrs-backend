package com.example.coursemanagement.service;

import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.repository.ClassScheduleRepository;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.service.impl.ClassScheduleServiceImpl;
import com.example.coursemanagement.strategy.VacancyFilterStrategy;
import com.example.coursemanagement.strategy.impl.FullClassesStrategy;
import com.example.coursemanagement.strategy.impl.MostlyEmptyClassesStrategy;
import com.example.coursemanagement.strategy.impl.NearFullClassesStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassScheduleServiceImplTest {

    @Mock
    private ClassScheduleRepository classScheduleRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private ClassScheduleServiceImpl classScheduleService;

    private Course course;
    private ClassSchedule classSchedule;
    private LocalTime startTime;
    private LocalTime endTime;

    @BeforeEach
    public void setup() {
        startTime = LocalTime.of(9, 0);
        endTime = LocalTime.of(11, 0);

        course = new Course();
        course.setCourseId(1);
        course.setCourseCode("CS101");
        course.setCourseName("Introduction to Programming");
        course.setMaxCapacity(100);

        classSchedule = new ClassSchedule();
        classSchedule.setClassId(1);
        classSchedule.setCourse(course);
        classSchedule.setDayOfWeek("Monday");
        classSchedule.setStartTime(startTime);
        classSchedule.setEndTime(endTime);
        classSchedule.setMaxCapacity(30);
        classSchedule.setVacancy(20);
    }

    @Test
    public void testGetAllClassSchedulesForCourse_Success() {
        // Arrange
        List<ClassSchedule> schedules = Arrays.asList(classSchedule);
        when(classScheduleRepository.getAllClassSchedulesForCourse(1)).thenReturn(schedules);

        // Act
        List<ClassSchedule> result = classScheduleService.getAllClassSchedulesForCourse(1);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Monday", result.get(0).getDayOfWeek());
        verify(classScheduleRepository).getAllClassSchedulesForCourse(1);
    }

    @Test
    public void testGetAllClassSchedulesForCourse_NotFound() {
        // Arrange
        when(classScheduleRepository.getAllClassSchedulesForCourse(999)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> classScheduleService.getAllClassSchedulesForCourse(999));

        assertTrue(exception.getMessage().contains("not found"));
        verify(classScheduleRepository).getAllClassSchedulesForCourse(999);
    }

    @Test
    public void testGetClassScheduleById_Success() {
        // Arrange
        when(classScheduleRepository.getClassScheduleById(1)).thenReturn(classSchedule);

        // Act
        ClassSchedule result = classScheduleService.getClassScheduleById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getClassId());
        assertEquals("Monday", result.getDayOfWeek());
        verify(classScheduleRepository).getClassScheduleById(1);
    }

    @Test
    public void testGetClassScheduleById_NotFound() {
        // Arrange
        when(classScheduleRepository.getClassScheduleById(999)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> classScheduleService.getClassScheduleById(999));

        assertTrue(exception.getMessage().contains("not found"));
        verify(classScheduleRepository).getClassScheduleById(999);
    }

    @Test
    public void testAddClassSchedule_Success() {
        // Arrange
        when(classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime)).thenReturn(false);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(classScheduleRepository.getAllClassSchedulesForCourse(1)).thenReturn(new ArrayList<>());
        when(classScheduleRepository.save(any(ClassSchedule.class))).thenReturn(classSchedule);

        // Act
        ClassSchedule result = classScheduleService.addClassSchedule(classSchedule);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getClassId());
        assertEquals("Monday", result.getDayOfWeek());
        verify(classScheduleRepository).existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime);
        verify(courseRepository).findById(1);
        verify(classScheduleRepository).getAllClassSchedulesForCourse(1);
        verify(classScheduleRepository).save(classSchedule);
    }

    @Test
    public void testAddClassSchedule_DuplicateSchedule() {
        // Arrange
        when(classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime)).thenReturn(true);

        // Act & Assert
        DuplicateIDException exception = assertThrows(
                DuplicateIDException.class,
                () -> classScheduleService.addClassSchedule(classSchedule));

        assertTrue(exception.getMessage().contains(classSchedule.toString()));
        verify(classScheduleRepository).existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime);
        verify(classScheduleRepository, never()).save(any(ClassSchedule.class));
    }

    @Test
    public void testAddClassSchedule_InvalidCapacity() {
        // Arrange
        classSchedule.setVacancy(40); // More than max capacity (30)
        when(classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime)).thenReturn(false);

        // Act & Assert
        InvalidCapacityException exception = assertThrows(
                InvalidCapacityException.class,
                () -> classScheduleService.addClassSchedule(classSchedule));

        assertEquals("Vacancy cannot be more than max capacity", exception.getMessage());
        verify(classScheduleRepository).existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime);
        verify(classScheduleRepository, never()).save(any(ClassSchedule.class));
    }

    @Test
    public void testAddClassSchedule_InvalidTimeRange() {
        // Arrange
        classSchedule.setStartTime(LocalTime.of(11, 0));
        classSchedule.setEndTime(LocalTime.of(9, 0));
        when(classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", LocalTime.of(11, 0), LocalTime.of(9, 0))).thenReturn(false);

        // Act & Assert
        InvalidDateException exception = assertThrows(
                InvalidDateException.class,
                () -> classScheduleService.addClassSchedule(classSchedule));

        assertEquals("Start date cannot be after end date", exception.getMessage());
        verify(classScheduleRepository).existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", LocalTime.of(11, 0), LocalTime.of(9, 0));
        verify(classScheduleRepository, never()).save(any(ClassSchedule.class));
    }

    @Test
    public void testAddClassSchedule_ExceedsCourseCapacity() {
        // Arrange
        when(classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime)).thenReturn(false);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        // Create existing class schedules that already use up all the course capacity
        ClassSchedule existingSchedule = new ClassSchedule();
        existingSchedule.setClassId(2);
        existingSchedule.setCourse(course);
        existingSchedule.setMaxCapacity(100); // Uses up all course capacity

        List<ClassSchedule> existingSchedules = Arrays.asList(existingSchedule);
        when(classScheduleRepository.getAllClassSchedulesForCourse(1)).thenReturn(existingSchedules);

        // Act & Assert
        InvalidCapacityException exception = assertThrows(
                InvalidCapacityException.class,
                () -> classScheduleService.addClassSchedule(classSchedule));

        assertTrue(exception.getMessage().contains("exceeds course maximum capacity"));
        verify(classScheduleRepository).existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime);
        verify(courseRepository).findById(1);
        verify(classScheduleRepository).getAllClassSchedulesForCourse(1);
        verify(classScheduleRepository, never()).save(any(ClassSchedule.class));
    }

    @Test
    public void testEditClassSchedule_Success() {
        // Arrange
        ClassSchedule updatedSchedule = new ClassSchedule();
        updatedSchedule.setClassId(1);
        updatedSchedule.setCourse(course);
        updatedSchedule.setDayOfWeek("Tuesday");
        updatedSchedule.setStartTime(LocalTime.of(14, 0));
        updatedSchedule.setEndTime(LocalTime.of(16, 0));
        updatedSchedule.setMaxCapacity(25);
        updatedSchedule.setVacancy(15);

        when(classScheduleRepository.getClassScheduleById(1)).thenReturn(classSchedule);
        when(classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTimeAndClassIdNot(
                eq(1), eq("Tuesday"), any(), any(), eq(1))).thenReturn(false);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(classScheduleRepository.getAllClassSchedulesForCourse(1)).thenReturn(new ArrayList<>());
        when(classScheduleRepository.save(any(ClassSchedule.class))).thenReturn(updatedSchedule);

        // Act
        ClassSchedule result = classScheduleService.editClassSchedule(updatedSchedule);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getClassId());
        assertEquals("Tuesday", result.getDayOfWeek());
        verify(classScheduleRepository).getClassScheduleById(1);
        verify(classScheduleRepository).existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTimeAndClassIdNot(
                eq(1), eq("Tuesday"), any(), any(), eq(1));
        verify(courseRepository).findById(1);
        verify(classScheduleRepository).getAllClassSchedulesForCourse(1);
        verify(classScheduleRepository).save(any(ClassSchedule.class));
    }

    @Test
    public void testDeleteClassSchedule_Success() {
        // Arrange
        when(classScheduleRepository.getClassScheduleById(1)).thenReturn(classSchedule);
        doNothing().when(classScheduleRepository).delete(classSchedule);

        // Act
        boolean result = classScheduleService.deleteClassSchedule(1);

        // Assert
        assertTrue(result);
        verify(classScheduleRepository).getClassScheduleById(1);
        verify(classScheduleRepository).delete(classSchedule);
    }

    @Test
    public void testDeleteClassSchedule_NotFound() {
        // Arrange
        when(classScheduleRepository.getClassScheduleById(999)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> classScheduleService.deleteClassSchedule(999));

        assertTrue(exception.getMessage().contains("not found"));
        verify(classScheduleRepository).getClassScheduleById(999);
        verify(classScheduleRepository, never()).delete(any(ClassSchedule.class));
    }

    @Test
    public void testExistsByCourseAndDayOfWeekAndStartTimeAndEndTime() {
        // Arrange
        when(classScheduleRepository.existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime)).thenReturn(true);

        // Act
        boolean result = classScheduleService.existsByCourseAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime);

        // Assert
        assertTrue(result);
        verify(classScheduleRepository).existsByCourse_CourseIdAndDayOfWeekAndStartTimeAndEndTime(
                1, "Monday", startTime, endTime);
    }

    @Test
    public void testGetClassesByVacancyFilter() {
        // Arrange
        List<ClassSchedule> allSchedules = Arrays.asList(classSchedule);
        when(classScheduleRepository.findAll()).thenReturn(allSchedules);

        // Create a test strategy that matches everything
        VacancyFilterStrategy testStrategy = new VacancyFilterStrategy() {
            @Override
            public boolean matches(ClassSchedule schedule) {
                return true;
            }

            @Override
            public String getTitle() {
                return "Test Strategy (All)";
            }
        };

        // Act
        List<ClassSchedule> result = classScheduleService.getClassesByVacancyFilter(testStrategy);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getClassId());
        verify(classScheduleRepository).findAll();
    }

    @Test
    public void testGetClassesByVacancyFilter_NoMatches() {
        // Arrange
        List<ClassSchedule> allSchedules = Arrays.asList(classSchedule);
        when(classScheduleRepository.findAll()).thenReturn(allSchedules);

        // Create a test strategy that matches nothing
        VacancyFilterStrategy testStrategy = new VacancyFilterStrategy() {
            @Override
            public boolean matches(ClassSchedule schedule) {
                return false;
            }

            @Override
            public String getTitle() {
                return "Test Strategy (None)";
            }
        };

        // Act
        List<ClassSchedule> result = classScheduleService.getClassesByVacancyFilter(testStrategy);

        // Assert
        assertTrue(result.isEmpty());
        verify(classScheduleRepository).findAll();
    }
}