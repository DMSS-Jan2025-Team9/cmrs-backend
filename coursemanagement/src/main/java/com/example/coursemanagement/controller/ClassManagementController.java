package com.example.coursemanagement.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursemanagement.dto.ClassScheduleDTO;
import com.example.coursemanagement.dto.ErrorResponse;
import com.example.coursemanagement.exception.DuplicateIDException;
import com.example.coursemanagement.exception.InvalidCapacityException;
import com.example.coursemanagement.exception.InvalidDateException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.model.ClassSchedule;
import com.example.coursemanagement.service.ClassScheduleService;
import com.example.coursemanagement.strategy.VacancyFilterStrategy;
import com.example.coursemanagement.strategy.impl.FullClassesStrategy;
import com.example.coursemanagement.strategy.impl.MostlyEmptyClassesStrategy;
import com.example.coursemanagement.strategy.impl.NearFullClassesStrategy;


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
            if (classScheduleDTO.getDayOfWeek() != null) {
                existingClassSchedule.setDayOfWeek(classScheduleDTO.getDayOfWeek());
            }
            if (classScheduleDTO.getStartTime() != null) {
                existingClassSchedule.setStartTime(classScheduleDTO.getStartTime());
            }
            if (classScheduleDTO.getEndTime() != null) {
                existingClassSchedule.setEndTime(classScheduleDTO.getEndTime());
            }
            existingClassSchedule.setMaxCapacity(classScheduleDTO.getMaxCapacity());
            existingClassSchedule.setVacancy(classScheduleDTO.getVacancy());
            
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

    @DeleteMapping("/deleteClassSchedule/{classId}")
    public ResponseEntity<?> deleteClassSchedule(@PathVariable int classId) {
        try {
            boolean deleted = classScheduleService.deleteClassSchedule(classId);
            if (deleted) {
                return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Class schedule deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Delete failed", "Class schedule not found"));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Class schedule not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Delete failed", e.getMessage()));
        }
    }

    // @GetMapping("/full")
    // public ResponseEntity<List<ClassScheduleDTO>> getFullClasses() {
    //     try {
    //         List<ClassSchedule> fullClasses = classScheduleService.getFullClasses();
    //         List<ClassScheduleDTO> fullClassesDTO = fullClasses.stream()
    //                 .map(classSchedule -> modelMapper.map(classSchedule, ClassScheduleDTO.class))
    //                 .toList();
    //         return ResponseEntity.ok().body(fullClassesDTO);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    //     }
    // }
    
    // @GetMapping("/nearFull")
    // public ResponseEntity<List<ClassScheduleDTO>> getNearFullClasses() {
    //     try {
    //         List<ClassSchedule> nearFullClasses = classScheduleService.getNearFullClasses();
    //         List<ClassScheduleDTO> nearFullClassesDTO = nearFullClasses.stream()
    //                 .map(classSchedule -> modelMapper.map(classSchedule, ClassScheduleDTO.class))
    //                 .toList();
    //         return ResponseEntity.ok().body(nearFullClassesDTO);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    //     }
    // }
    
    // @GetMapping("/mostlyEmpty")
    // public ResponseEntity<List<ClassScheduleDTO>> getMostlyEmptyClasses() {
    //     try {
    //         List<ClassSchedule> mostlyEmptyClasses = classScheduleService.getMostlyEmptyClasses();
    //         List<ClassScheduleDTO> mostlyEmptyClassesDTO = mostlyEmptyClasses.stream()
    //                 .map(classSchedule -> modelMapper.map(classSchedule, ClassScheduleDTO.class))
    //                 .toList();
    //         return ResponseEntity.ok().body(mostlyEmptyClassesDTO);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    //     }
    // }

    @GetMapping("/filter")
    public ResponseEntity<List<ClassScheduleDTO>> getFilteredClasses(@RequestParam String filterType) {
        VacancyFilterStrategy strategy;
        switch (filterType.toLowerCase()) {
            case "full":
                strategy = new FullClassesStrategy();
                break;
            case "nearfull":
                strategy = new NearFullClassesStrategy();
                break;
            case "mostlyempty":
                strategy = new MostlyEmptyClassesStrategy();
                break;
            default:
                return ResponseEntity.badRequest().body(null);
        }
        
        List<ClassSchedule> filteredClasses = classScheduleService.getClassesByVacancyFilter(strategy);
        List<ClassScheduleDTO> filteredClassesDTO = filteredClasses.stream()
                .map(classSchedule -> modelMapper.map(classSchedule, ClassScheduleDTO.class))
                .toList();
        return ResponseEntity.ok().body(filteredClassesDTO);
    }
}

