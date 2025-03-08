package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.ClassScheduleDTO;
import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.service.ClassScheduleService;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/classSchedule")
public class ClassManagementController {

    private final ModelMapper modelMapper;

    private final ClassScheduleService classScheduleService;

    public ClassManagementController(ClassScheduleService classScheduleService, ModelMapper modelMapper) {
        super();
        this.classScheduleService = classScheduleService;
        this.modelMapper = modelMapper;
    }

    // Get all Courses
    @GetMapping
    public List<ClassScheduleDTO> getAllClassSchedulesForCourse(int courseId) {
        return classScheduleService.getAllClassSchedulesForCourse(courseId).stream().map(classSchedule -> modelMapper.map(classSchedule, ClassScheduleDTO.class))
            .toList();
    }

    // Get course by course code code
    @GetMapping("/classId/{classId}")
    public ResponseEntity<ClassScheduleDTO> getCourseById(@PathVariable Integer classId) {
        ClassSchedule classSchedule = classScheduleService.getClassScheduleById(classId);
        ClassScheduleDTO classScheduleDTO = modelMapper.map(classSchedule, ClassScheduleDTO.class);
        return ResponseEntity.ok().body(classScheduleDTO);
    }

    @PostMapping("/addClassSchedule")
    public ResponseEntity<ClassScheduleDTO> addClassSchedule(@RequestBody ClassScheduleDTO classScheduleDTO) {
        ClassSchedule classSchedule = modelMapper.map(classScheduleDTO, ClassSchedule.class); // Map DTO to entity
        ClassSchedule newClassSchedule = classScheduleService.addClassSchedule(classSchedule); // Save course
        ClassScheduleDTO newClassScheduleDTO = modelMapper.map(newClassSchedule, ClassScheduleDTO.class); // Map entity to DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(newClassScheduleDTO); // Return DTO with status 201
    }

    @PutMapping("/editClassSchedule/{classId}")
    public ResponseEntity<ClassScheduleDTO> editCourse(@PathVariable int classId, @RequestBody ClassScheduleDTO classScheduleDTO) {
        ClassSchedule existingClassSchedule = classScheduleService.getClassScheduleById(classId);
        if (existingClassSchedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        modelMapper.map(classScheduleDTO, existingClassSchedule); // Map updated fields to existing entity
        ClassSchedule updatedClassSchedule = classScheduleService.editClassSchedule(existingClassSchedule); // Update course
        ClassScheduleDTO updatedClassScheduleDTO = modelMapper.map(updatedClassSchedule, ClassScheduleDTO.class); // Map entity to DTO
        return ResponseEntity.ok().body(updatedClassScheduleDTO); // Return updated DTO
    }
}

