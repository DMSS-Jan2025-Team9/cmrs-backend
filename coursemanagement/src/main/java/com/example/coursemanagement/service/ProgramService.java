package com.example.coursemanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.coursemanagement.model.Program;
import com.example.coursemanagement.repository.ProgramRepository;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    @Autowired
    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    // Method to fetch program details by programId
    public Program getProgramById(Integer programId) {
        // Check if program exists by programId
        Optional<Program> program = programRepository.findById(programId);
        if (program.isPresent()) {
            return program.get(); // Return the Program details if found
        } else {
            throw new RuntimeException("Program not found with ID: " + programId); // Handle case where Program is not found
        }
    }

    public List<Program> getAllPrograms() {
        return programRepository.findAll(); // Fetches all programs from the database
    }

}
