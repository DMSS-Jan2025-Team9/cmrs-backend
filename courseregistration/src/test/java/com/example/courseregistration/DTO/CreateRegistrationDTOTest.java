package com.example.courseregistration.DTO;
import com.example.courseregistration.dto.CreateRegistrationDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateRegistrationDTOTest {

    @Test
    void gettersAndSetters_shouldWorkProperly() {
        CreateRegistrationDTO dto = new CreateRegistrationDTO();

        // Test setting and getting studentFullIds
        List<String> ids = Arrays.asList("student1", "student2");
        dto.setStudentFullIds(ids);
        assertEquals(ids, dto.getStudentFullIds(), "getStudentFullIds should return the list set via setStudentFullIds");

        // Test setting and getting classId
        Long classId = 123L;
        dto.setClassId(classId);
        assertEquals(classId, dto.getClassId(), "getClassId should return the value set via setClassId");
    }

    @Test
    void defaultValues_shouldBeNull() {
        CreateRegistrationDTO dto = new CreateRegistrationDTO();
        assertNull(dto.getStudentFullIds(), "studentFullIds should be null by default");
        assertNull(dto.getClassId(), "classId should be null by default");
    }

    @Test
    void studentFullIds_setEmptyList_shouldReturnEmptyList() {
        CreateRegistrationDTO dto = new CreateRegistrationDTO();
        List<String> empty = Collections.emptyList();
        dto.setStudentFullIds(empty);
        assertSame(empty, dto.getStudentFullIds(), "setStudentFullIds should preserve the exact list instance");
    }
}
