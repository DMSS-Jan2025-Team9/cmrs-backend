package com.example.usermanagement.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProgramResponseTest {

    @Test
    public void testProgramResponseGettersAndSetters() {
        // Arrange
        ProgramResponse programResponse = new ProgramResponse();
        Long programId = 1L;
        String programName = "Computer Science";
        String programDesc = "Bachelor of Computer Science";
        List<Course> courses = new ArrayList<>();
        courses.add(new Course());

        // Act
        programResponse.setProgramId(programId);
        programResponse.setProgramName(programName);
        programResponse.setProgramDesc(programDesc);
        programResponse.setCourses(courses);

        // Assert
        assertEquals(programId, programResponse.getProgramId());
        assertEquals(programName, programResponse.getProgramName());
        assertEquals(programDesc, programResponse.getProgramDesc());
        assertEquals(courses, programResponse.getCourses());
    }
}