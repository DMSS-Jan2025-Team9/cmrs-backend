package com.example.coursemanagement.controller;
import org.springframework.http.ResponseEntity;
import com.example.coursemanagement.dto.ClassDTO;
import com.example.coursemanagement.service.ClassService;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/classes")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping("/{classId}")
    public ResponseEntity<ClassDTO> getClass(@PathVariable Long classId) {
        ClassDTO classDto = classService.getClassById(classId);
        if (classDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(classDto);
    }


    @GetMapping
    public List<ClassDTO> getAllClasses(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer maxCapacity,
            @RequestParam(required = false) String dayOfWeek) {
        return classService.filterClasses(courseId, classId, maxCapacity, dayOfWeek);
    }

    @PutMapping("/{classId}")
    public ClassDTO updateClass(@PathVariable Long classId, @RequestBody ClassDTO classDTO) {
        return classService.updateClass(classId, classDTO);
    }

}