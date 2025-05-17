package com.example.coursemanagement.service;

import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.model.Program;
import com.example.coursemanagement.model.ProgramCourse;
import com.example.coursemanagement.repository.ClassScheduleRepository;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.ProgramCourseRepository;
import com.example.coursemanagement.repository.ProgramRepository;
import com.example.coursemanagement.service.impl.CourseServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private ProgramCourseRepository programCourseRepository;

    @Mock
    private ClassScheduleRepository classScheduleRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private Program program;
    private ProgramCourse programCourse;
    private Date now;
    private Date future;

    @BeforeEach
    public void setup() {
        now = new Date();
        future = new Date(now.getTime() + 1000000);

        course = new Course();
        course.setCourseId(1);
        course.setCourseCode("CS101");
        course.setCourseName("Introduction to Programming");
        course.setCourseDesc("Basic programming concepts");
        course.setMaxCapacity(100);
        course.setStatus("active");
        course.setRegistrationStart(now);
        course.setRegistrationEnd(future);

        program = new Program();
        program.setProgramId(1);
        program.setProgramName("Computer Science");
        program.setProgramDesc("Computer Science Program");

        programCourse = new ProgramCourse();
        programCourse.setCourseId(1);
        programCourse.setProgramId(1);
    }

    @Test
    public void testGetAllCourses() {
        // Arrange
        List<Course> courses = Arrays.asList(course);
        when(courseRepository.findAll()).thenReturn(courses);

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getCourseCode());
        verify(courseRepository).findAll();
    }

    @Test
    public void testFindAllActiveCourses() {
        // Arrange
        List<Course> activeCourses = Arrays.asList(course);
        when(courseRepository.findByStatusAndRegistrationStartBeforeAndRegistrationEndAfter(
                eq("active"), any(Date.class), any(Date.class))).thenReturn(activeCourses);

        // Act
        List<Course> result = courseService.findAllActiveCourses();

        // Assert
        assertEquals(1, result.size());
        assertEquals("active", result.get(0).getStatus());
        verify(courseRepository).findByStatusAndRegistrationStartBeforeAndRegistrationEndAfter(
                eq("active"), any(Date.class), any(Date.class));
    }

    @Test
    public void testGetCourse_Success() {
        // Arrange
        when(courseRepository.getCourse("CS101")).thenReturn(course);

        // Act
        Course result = courseService.getCourse("CS101");

        // Assert
        assertNotNull(result);
        assertEquals("CS101", result.getCourseCode());
        verify(courseRepository).getCourse("CS101");
    }

    @Test
    public void testGetCourse_NotFound() {
        // Arrange
        when(courseRepository.getCourse("NONEXISTENT")).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseService.getCourse("NONEXISTENT"));

        assertTrue(exception.getMessage().contains("not found"));
        verify(courseRepository).getCourse("NONEXISTENT");
    }

    @Test
    public void testGetCourseWithProgram() {
        // Arrange
        when(courseRepository.getCourse("CS101")).thenReturn(course);

        // Act
        Course result = courseService.getCourseWithProgram("CS101");

        // Assert
        assertNotNull(result);
        assertEquals("CS101", result.getCourseCode());
        verify(courseRepository).getCourse("CS101");
    }

    @Test
    public void testGetCourseById_Success() {
        // Arrange
        when(courseRepository.getCourseById(1)).thenReturn(course);

        // Act
        Course result = courseService.getCourseById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCourseId());
        verify(courseRepository).getCourseById(1);
    }

    @Test
    public void testGetCourseById_NotFound() {
        // Arrange
        when(courseRepository.getCourseById(999)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseService.getCourseById(999));

        assertTrue(exception.getMessage().contains("not found"));
        verify(courseRepository).getCourseById(999);
    }

    @Test
    public void testSearchCourse() {
        // Arrange
        List<Course> courses = Arrays.asList(course);
        when(courseRepository.searchCourse("CS", "Programming")).thenReturn(courses);

        // Act
        List<Course> result = courseService.searchCourse("CS", "Programming");

        // Assert
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getCourseCode());
        verify(courseRepository).searchCourse("CS", "Programming");
    }

    @Test
    public void testAddCourse_Success() {
        // Arrange
        when(courseRepository.existsByCourseCode("CS101")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        Course result = courseService.addCourse(course);

        // Assert
        assertNotNull(result);
        assertEquals("CS101", result.getCourseCode());
        verify(courseRepository).existsByCourseCode("CS101");
        verify(courseRepository).save(course);
    }

    @Test
    public void testAddCourse_DuplicateCode() {
        // Arrange
        when(courseRepository.existsByCourseCode("CS101")).thenReturn(true);

        // Act & Assert
        DuplicateIDException exception = assertThrows(
                DuplicateIDException.class,
                () -> courseService.addCourse(course));

        assertEquals("Course code already exists", exception.getMessage());
        verify(courseRepository).existsByCourseCode("CS101");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void testAddCourse_InvalidCapacity() {
        // Arrange
        course.setMaxCapacity(0);

        // Act & Assert
        InvalidCapacityException exception = assertThrows(
                InvalidCapacityException.class,
                () -> courseService.addCourse(course));

        assertEquals("Capacity must be a positive number", exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void testAddCourse_InvalidDateRange() {
        // Arrange
        course.setRegistrationStart(future);
        course.setRegistrationEnd(now);

        // Act & Assert
        InvalidDateException exception = assertThrows(
                InvalidDateException.class,
                () -> courseService.addCourse(course));

        assertEquals("Start date cannot be after end date", exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void testEditCourse_Success() {
        // Arrange
        Course updatedCourse = new Course();
        updatedCourse.setCourseId(1);
        updatedCourse.setCourseCode("CS101");
        updatedCourse.setCourseName("Updated Course");
        updatedCourse.setCourseDesc("Updated Description");
        updatedCourse.setMaxCapacity(200);
        updatedCourse.setStatus("inactive");
        updatedCourse.setRegistrationStart(now);
        updatedCourse.setRegistrationEnd(future);

        when(courseRepository.getCourseById(1)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // Act
        Course result = courseService.editCourse(updatedCourse);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Course", result.getCourseName());
        assertEquals("inactive", result.getStatus());
        verify(courseRepository).getCourseById(1);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    public void testEditCourse_NotFound() {
        // Arrange
        when(courseRepository.getCourseById(1)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseService.editCourse(course));

        assertTrue(exception.getMessage().contains("not found"));
        verify(courseRepository).getCourseById(1);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void testEditCourseWithProgram_Success() {
        // Arrange
        when(courseRepository.getCourseById(1)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(programRepository.findById(1)).thenReturn(Optional.of(program));

        List<ProgramCourse> existingAssociations = Arrays.asList(programCourse);
        when(programCourseRepository.findByCourseId(1)).thenReturn(existingAssociations);

        // Act
        Course result = courseService.editCourseWithProgram(course, 1);

        // Assert
        assertNotNull(result);
        verify(courseRepository).getCourseById(1);
        verify(courseRepository).save(any(Course.class));
        verify(programRepository).findById(1);
        verify(programCourseRepository).findByCourseId(1);
    }

    @Test
    public void testEditCourseWithProgram_ChangeProgramId() {
        // Arrange
        ProgramCourse oldProgramCourse = new ProgramCourse();
        oldProgramCourse.setCourseId(1);
        oldProgramCourse.setProgramId(2);

        when(courseRepository.getCourseById(1)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(programRepository.findById(1)).thenReturn(Optional.of(program));

        List<ProgramCourse> existingAssociations = Arrays.asList(oldProgramCourse);
        when(programCourseRepository.findByCourseId(1)).thenReturn(existingAssociations);

        // Act
        Course result = courseService.editCourseWithProgram(course, 1);

        // Assert
        assertNotNull(result);
        verify(courseRepository).getCourseById(1);
        verify(courseRepository).save(any(Course.class));
        verify(programRepository).findById(1);
        verify(programCourseRepository).findByCourseId(1);
        verify(programCourseRepository).deleteByCourseId(1);
        verify(programCourseRepository).save(any(ProgramCourse.class));
    }

    @Test
    public void testEditCourseWithProgram_NoPreviousAssociation() {
        // Arrange
        when(courseRepository.getCourseById(1)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(programRepository.findById(1)).thenReturn(Optional.of(program));

        when(programCourseRepository.findByCourseId(1)).thenReturn(new ArrayList<>());

        // Act
        Course result = courseService.editCourseWithProgram(course, 1);

        // Assert
        assertNotNull(result);
        verify(courseRepository).getCourseById(1);
        verify(courseRepository).save(any(Course.class));
        verify(programRepository).findById(1);
        verify(programCourseRepository).findByCourseId(1);
        verify(programCourseRepository, never()).deleteByCourseId(anyInt());
        verify(programCourseRepository).save(any(ProgramCourse.class));
    }

    @Test
    public void testAddCourseWithProgram_Success() {
        // Arrange
        when(courseRepository.existsByCourseCode("CS101")).thenReturn(false);
        when(programRepository.findById(1)).thenReturn(Optional.of(program));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        Course result = courseService.addCourse(course, 1);

        // Assert
        assertNotNull(result);
        assertEquals("CS101", result.getCourseCode());
        verify(courseRepository).existsByCourseCode("CS101");
        verify(programRepository).findById(1);
        verify(courseRepository).save(course);
        verify(programCourseRepository).save(any(ProgramCourse.class));
    }

    @Test
    public void testGetProgramIdForCourse_Success() {
        // Arrange
        List<ProgramCourse> programCourses = Arrays.asList(programCourse);
        when(programCourseRepository.findByCourseId(1)).thenReturn(programCourses);

        // Act
        Integer result = courseService.getProgramIdForCourse(1);

        // Assert
        assertEquals(1, result);
        verify(programCourseRepository).findByCourseId(1);
    }

    @Test
    public void testGetProgramIdForCourse_NotFound() {
        // Arrange
        when(programCourseRepository.findByCourseId(1)).thenReturn(new ArrayList<>());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseService.getProgramIdForCourse(1));

        assertTrue(exception.getMessage().contains("not found"));
        verify(programCourseRepository).findByCourseId(1);
    }

    @Test
    public void testDeleteCourse_Success() {
        // Arrange
        when(courseRepository.getCourseById(1)).thenReturn(course);
        doNothing().when(classScheduleRepository).deleteAllByCourseId(1);
        doNothing().when(programCourseRepository).deleteAllByCourseId(1);
        doNothing().when(courseRepository).delete(course);

        // Act
        courseService.deleteCourse(1);

        // Assert
        verify(courseRepository).getCourseById(1);
        verify(classScheduleRepository).deleteAllByCourseId(1);
        verify(programCourseRepository).deleteAllByCourseId(1);
        verify(courseRepository).delete(course);
    }

    @Test
    public void testDeleteCourse_NotFound() {
        // Arrange
        when(courseRepository.getCourseById(999)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseService.deleteCourse(999));

        assertTrue(exception.getMessage().contains("not found"));
        verify(courseRepository).getCourseById(999);
        verify(classScheduleRepository, never()).deleteAllByCourseId(anyInt());
        verify(programCourseRepository, never()).deleteAllByCourseId(anyInt());
        verify(courseRepository, never()).delete(any(Course.class));
    }
}