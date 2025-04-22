package com.example.coursemanagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.dto.ProgramDto;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.model.Program;
import com.example.coursemanagement.repository.ProgramRepository;
import com.example.coursemanagement.service.ProgramService;

@Service
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;

    @Autowired
    public ProgramServiceImpl(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    // Method to fetch program details by programId
    public ProgramDto getProgramById(Integer programId) {
        return programRepository.findById(programId)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Program not found with ID: " + programId));
    }

    public List<ProgramDto> getAllPrograms() {
        return programRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProgramDto mapToDto(Program program) {
        List<CourseDTO> courseDTOs = program.getCourses().stream()
                .map(this::mapCourseToDto)
                .collect(Collectors.toList());

        return new ProgramDto(
                program.getProgramId(),
                program.getProgramName(),
                program.getProgramDesc(),
                courseDTOs
        );
    }

    public CourseDTO mapCourseToDto(Course course) {
        return new CourseDTO(
                course.getCourseId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getRegistrationStart(),
                course.getRegistrationEnd(),
                course.getMaxCapacity(),
                course.getStatus(),
                course.getCourseDesc()
        );
    }



}
