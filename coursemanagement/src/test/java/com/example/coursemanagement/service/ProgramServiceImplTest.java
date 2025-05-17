package com.example.coursemanagement.service;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.dto.ProgramDto;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.model.Program;
import com.example.coursemanagement.repository.ProgramRepository;
import com.example.coursemanagement.service.impl.ProgramServiceImpl;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgramServiceImplTest {

    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private ProgramServiceImpl programService;

    private Program program;
    private Course course;
    private Date now;
    private Date future;

    @BeforeEach
    public void setup() {
        now = new Date();
        future = new Date(now.getTime() + 1000000);

        program = new Program();
        program.setProgramId(1);
        program.setProgramName("Computer Science");
        program.setProgramDesc("Computer Science Program");

        course = new Course();
        course.setCourseId(1);
        course.setCourseCode("CS101");
        course.setCourseName("Introduction to Programming");
        course.setCourseDesc("Basic programming concepts");
        course.setMaxCapacity(100);
        course.setStatus("active");
        course.setRegistrationStart(now);
        course.setRegistrationEnd(future);

        program.setCourses(Arrays.asList(course));
    }

    @Test
    public void testGetProgramById_Success() {
        // Arrange
        when(programRepository.findById(1)).thenReturn(Optional.of(program));

        // Act
        ProgramDto result = programService.getProgramById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProgramId());
        assertEquals("Computer Science", result.getProgramName());
        assertEquals(1, result.getCourses().size());
        verify(programRepository).findById(1);
    }

    @Test
    public void testGetProgramById_NotFound() {
        // Arrange
        when(programRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> programService.getProgramById(999));

        assertTrue(exception.getMessage().contains("not found"));
        verify(programRepository).findById(999);
    }

    @Test
    public void testGetAllPrograms() {
        // Arrange
        List<Program> programs = Arrays.asList(program);
        when(programRepository.findAll()).thenReturn(programs);

        // Act
        List<ProgramDto> result = programService.getAllPrograms();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Computer Science", result.get(0).getProgramName());
        assertEquals(1, result.get(0).getCourses().size());
        verify(programRepository).findAll();
    }

    @Test
    public void testGetAllPrograms_Empty() {
        // Arrange
        when(programRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<ProgramDto> result = programService.getAllPrograms();

        // Assert
        assertTrue(result.isEmpty());
        verify(programRepository).findAll();
    }

    @Test
    public void testMapToDto() {
        // Act
        ProgramDto result = programService.mapToDto(program);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProgramId());
        assertEquals("Computer Science", result.getProgramName());
        assertEquals("Computer Science Program", result.getProgramDesc());
        assertEquals(1, result.getCourses().size());

        CourseDTO courseDTO = result.getCourses().get(0);
        assertEquals(1, courseDTO.getCourseId());
        assertEquals("CS101", courseDTO.getCourseCode());
        assertEquals("Introduction to Programming", courseDTO.getCourseName());
    }

    @Test
    public void testMapCourseToDto() {
        // Act
        CourseDTO result = programService.mapCourseToDto(course);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getCourseId());
        assertEquals("CS101", result.getCourseCode());
        assertEquals("Introduction to Programming", result.getCourseName());
        assertEquals("Basic programming concepts", result.getCourseDesc());
        assertEquals(100, result.getMaxCapacity());
        assertEquals("active", result.getStatus());
        assertEquals(now, result.getRegistrationStart());
        assertEquals(future, result.getRegistrationEnd());
    }

    @Test
    public void testGetCoursesByProgramId_Success() {
        // Arrange
        when(programRepository.findById(1)).thenReturn(Optional.of(program));

        // Act
        List<CourseDTO> result = programService.getCoursesByProgramId(1);

        // Assert
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getCourseCode());
        verify(programRepository).findById(1);
    }

    @Test
    public void testGetCoursesByProgramId_NoCourses() {
        // Arrange
        Program emptyProgram = new Program();
        emptyProgram.setProgramId(1);
        emptyProgram.setProgramName("Empty Program");
        emptyProgram.setCourses(new ArrayList<>());

        when(programRepository.findById(1)).thenReturn(Optional.of(emptyProgram));

        // Act
        List<CourseDTO> result = programService.getCoursesByProgramId(1);

        // Assert
        assertTrue(result.isEmpty());
        verify(programRepository).findById(1);
    }

    @Test
    public void testGetCoursesByProgramId_NotFound() {
        // Arrange
        when(programRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> programService.getCoursesByProgramId(999));

        assertTrue(exception.getMessage().contains("not found"));
        verify(programRepository).findById(999);
    }
}