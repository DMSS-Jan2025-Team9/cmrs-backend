package com.example.coursemanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursemanagement.dto.ProgramDto;
import com.example.coursemanagement.service.ProgramService;

@RestController
@RequestMapping("/api/program")
public class ProgramController {

    private final ProgramService programService;

    @Autowired
    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    // ✅ Return ProgramDto instead of entity
    @GetMapping("/{programId}")
    public ProgramDto getProgramById(@PathVariable Integer programId) {
        // Fetch the program details by programId using the ProgramService
        return programService.getProgramById(programId);
    }

    // ✅ Return list of ProgramDto
    @GetMapping
    public List<ProgramDto> getAllPrograms() {
        return programService.getAllPrograms();
    }
}

