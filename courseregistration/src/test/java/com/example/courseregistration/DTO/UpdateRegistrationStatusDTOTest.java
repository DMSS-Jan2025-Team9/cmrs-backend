package com.example.courseregistration.DTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateRegistrationStatusDTOTest {

    @Test
    void defaultConstructor_shouldInitializeDefaults() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();

        assertNull(dto.getId(), "id should be null by default");
        assertNull(dto.getNewStatus(), "newStatus should be null by default");
        assertEquals(0, dto.getIdentifier(), "identifier should be 0 by default");
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        Long expectedId = 55L;
        String expectedStatus = "Confirmed";
        int expectedIdentifier = 3;

        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO(expectedId, expectedStatus, expectedIdentifier);

        assertEquals(expectedId, dto.getId(), "getId should return value set by constructor");
        assertEquals(expectedStatus, dto.getNewStatus(), "getNewStatus should return value set by constructor");
        assertEquals(expectedIdentifier, dto.getIdentifier(), "getIdentifier should return value set by constructor");
    }

    @Test
    void setters_shouldUpdateFields() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();

        Long newId = 77L;
        dto.setId(newId);
        assertEquals(newId, dto.getId(), "setId should update id field");

        String newStatus = "Cancelled";
        dto.setNewStatus(newStatus);
        assertEquals(newStatus, dto.getNewStatus(), "setNewStatus should update newStatus field");

        int newIdentifier = 5;
        dto.setIdentifier(newIdentifier);
        assertEquals(newIdentifier, dto.getIdentifier(), "setIdentifier should update identifier field");
    }
}
