package com.example.coursemanagement.service;

import com.example.coursemanagement.dto.ClassDTO;
import com.example.coursemanagement.model.Class;
import com.example.coursemanagement.repository.ClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassService {

    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }


    public List<ClassDTO> filterClasses(Long courseId, Long classId, Integer maxCapacity, String dayOfWeek) {
        List<Class> classes = classRepository.filterClasses(courseId, classId, maxCapacity, dayOfWeek);
        return classes.stream()
                .map(courseClass -> new ClassDTO(
                        courseClass.getClassId(),
                        courseClass.getCourse().getCourseId(),
                        courseClass.getDayOfWeek(),
                        courseClass.getStartTime(),
                        courseClass.getEndTime(),
                        courseClass.getMaxCapacity(),
                        courseClass.getVacancy()
                ))
                .collect(Collectors.toList());
    }

    public ClassDTO getClassById(Long classId) {
        return classRepository.findById(classId)
                .map(courseClass -> new ClassDTO(
                        courseClass.getClassId(),
                        courseClass.getCourse().getCourseId(),
                        courseClass.getDayOfWeek(),
                        courseClass.getStartTime(),
                        courseClass.getEndTime(),
                        courseClass.getMaxCapacity(),
                        courseClass.getVacancy()
                ))
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
    }

    public ClassDTO updateClass(Long classId, ClassDTO updatedClassDTO) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
        
        // Update the fields. Adjust based on what fields are allowed to be updated.
        existingClass.setDayOfWeek(updatedClassDTO.getDayOfWeek());
        existingClass.setStartTime(updatedClassDTO.getStartTime());
        existingClass.setEndTime(updatedClassDTO.getEndTime());
        existingClass.setMaxCapacity(updatedClassDTO.getMaxCapacity());
        existingClass.setVacancy(updatedClassDTO.getVacancy());
        
        // Save the updated class entity
        Class savedClass = classRepository.save(existingClass);
        
        // Map the saved entity to a DTO and return it
        return new ClassDTO(
                savedClass.getClassId(),
                savedClass.getCourse().getCourseId(),
                savedClass.getDayOfWeek(),
                savedClass.getStartTime(),
                savedClass.getEndTime(),
                savedClass.getMaxCapacity(),
                savedClass.getVacancy());
    }
}