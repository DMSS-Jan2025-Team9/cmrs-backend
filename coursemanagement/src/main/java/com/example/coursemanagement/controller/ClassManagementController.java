package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.ClassScheduleDTO;
import com.example.coursemanagement.dto.ErrorResponse;
import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.service.ClassScheduleService;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
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

    // Get all Class Schedules of a course
    @GetMapping
    public List<ClassScheduleDTO> getAllClassSchedulesForCourse(int courseId) {
        try {
            List<ClassSchedule> classSchedules = classScheduleService.getAllClassSchedulesForCourse(courseId);
            if(classSchedules == null) {
                return List.of();
            }
            return classSchedules.stream().map(classSchedule -> modelMapper.map(classSchedule, ClassScheduleDTO.class)).toList();
        } catch (ResourceNotFoundException e) {
            return List.of();
        }
        catch (Exception e) {
            return List.of();
        }
    }

    // Get class schedule by course code 
    @GetMapping("/classId/{classId}")
    public ResponseEntity<ClassScheduleDTO> getCourseById(@PathVariable Integer classId) {
        try {
            ClassSchedule classSchedule = classScheduleService.getClassScheduleById(classId);
            ClassScheduleDTO classScheduleDTO = modelMapper.map(classSchedule, ClassScheduleDTO.class);
            return ResponseEntity.ok().body(classScheduleDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/addClassSchedule")
    public ResponseEntity<?> addClassSchedule(@RequestBody ClassScheduleDTO classScheduleDTO) {
        try {
            if (classScheduleService.existsByCourseAndDayOfWeekAndStartTimeAndEndTime(classScheduleDTO.getCourseId(), classScheduleDTO.getDayOfWeek(), classScheduleDTO.getStartTime(), classScheduleDTO.getEndTime())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
            else{
                ClassSchedule classSchedule = modelMapper.map(classScheduleDTO, ClassSchedule.class); // Map DTO to entity
                ClassSchedule newClassSchedule = classScheduleService.addClassSchedule(classSchedule); // Save course
                ClassScheduleDTO newClassScheduleDTO = modelMapper.map(newClassSchedule, ClassScheduleDTO.class); // Map entity to DTO
                return ResponseEntity.status(HttpStatus.CREATED).body(newClassScheduleDTO); // Return DTO with status 201
            }
        } catch (InvalidCapacityException | InvalidDateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation error", e.getMessage()));
        }catch(DuplicateIDException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Class Schedule with this code already exists", e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @PutMapping("/editClassSchedule/{classId}")
    public ResponseEntity<?> editCourse(@PathVariable int classId, @RequestBody ClassScheduleDTO classScheduleDTO) {
        try {
                ClassSchedule existingClassSchedule = classScheduleService.getClassScheduleById(classId);
            if (existingClassSchedule == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            modelMapper.map(classScheduleDTO, existingClassSchedule); // Map updated fields to existing entity
            ClassSchedule updatedClassSchedule = classScheduleService.editClassSchedule(existingClassSchedule); // Update course
            ClassScheduleDTO updatedClassScheduleDTO = modelMapper.map(updatedClassSchedule, ClassScheduleDTO.class); // Map entity to DTO
            return ResponseEntity.ok().body(updatedClassScheduleDTO); // Return updated DTO
        } catch (InvalidCapacityException | InvalidDateException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation error", e.getMessage()));
        } catch(DuplicateIDException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Class Schedule with this code already exists", e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }    
}

