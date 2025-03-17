package com.example.coursemanagement.controller;

import com.example.coursemanagement.model.Program;
import com.example.coursemanagement.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/program")
public class ProgramController {

    private final ProgramService programService;

    @Autowired
    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    // Get a program by programId
    @GetMapping("/{programId}")
    public Program getProgramById(@PathVariable Long programId) {
        // Fetch the program details by programId using the ProgramService
        return programService.getProgramById(programId);
    }
}

