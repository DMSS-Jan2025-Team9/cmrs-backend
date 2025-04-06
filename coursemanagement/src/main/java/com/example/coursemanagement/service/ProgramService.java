package com.example.coursemanagement.service;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.dto.ProgramDto;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.model.Program;
import com.example.coursemanagement.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    @Autowired
    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    // Method to fetch program details by programId
//    public Program getProgramById(Long programId) {
//        // Check if program exists by programId
//        Optional<Program> program = programRepository.findById(programId);
//        if (program.isPresent()) {
//            return program.get(); // Return the Program details if found
//        } else {
//            throw new RuntimeException("Program not found with ID: " + programId); // Handle case where Program is not found
//        }
//    }
//
//    public List<Program> getAllPrograms() {
//        return programRepository.findAll(); // Fetches all programs from the database
//    }

    public ProgramDto getProgramById(Long programId) {
        return programRepository.findById(programId)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Program not found with ID: " + programId));
    }

    public List<ProgramDto> getAllPrograms() {
        return programRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ProgramDto mapToDto(Program program) {
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

    private CourseDTO mapCourseToDto(Course course) {
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
