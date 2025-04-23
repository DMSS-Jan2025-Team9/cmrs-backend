package com.example.coursemanagement.service;

import java.util.List;

import com.example.coursemanagement.dto.CourseDTO;
import com.example.coursemanagement.dto.ProgramDto;
import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.model.Program;

public interface ProgramService {

    public ProgramDto getProgramById(Integer programId) ;
    public List<ProgramDto> getAllPrograms();
    public ProgramDto mapToDto(Program program);
    public CourseDTO mapCourseToDto(Course course);
}
