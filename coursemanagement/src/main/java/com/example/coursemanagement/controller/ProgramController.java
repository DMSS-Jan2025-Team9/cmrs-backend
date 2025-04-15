package com.example.coursemanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursemanagement.dto.ProgramDto;
import com.example.coursemanagement.model.Program;
import com.example.coursemanagement.service.ProgramService;

@RestController
@RequestMapping("/api/program")
public class ProgramController {

    private final ProgramService programService;

    @Autowired
    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    // Get a program by programId
//    @GetMapping("/{programId}")
//    public Program getProgramById(@PathVariable Long programId) {
//        // Fetch the program details by programId using the ProgramService
//        return programService.getProgramById(programId);
//    }
//
//    // Get all programs
//    @GetMapping
//    public List<Program> getAllPrograms() {
//        return programService.getAllPrograms();  // Calls the service method to fetch all programs
//    }


    // ✅ Return ProgramDto instead of entity
    @GetMapping("/{programId}")
    public Program getProgramById(@PathVariable Long programId) {
        // Fetch the program details by programId using the ProgramService
        return programService.getProgramById(programId);
    }

    // ✅ Return list of ProgramDto
    @GetMapping
    public List<ProgramDto> getAllPrograms() {
        return programService.getAllPrograms();
    }
}

