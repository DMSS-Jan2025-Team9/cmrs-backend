package com.example.courseregistration.service.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import com.example.courseregistration.service.NotificationPublisherService;
import com.example.courseregistration.service.WaitlistNotificationService;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class IndividualRegistrationStatusUpdateStrategyTest {

    @Mock
    private CourseRegistrationRepository repo;

    @Mock
    private MicroserviceClient client;

    @Mock
    private NotificationPublisherService notificationPublisherService;

    @Mock
    private WaitlistNotificationService waitlistNotificationService;

    @InjectMocks
    private IndividualRegistrationStatusUpdateStrategy strategy;

    private Registration reg;
    private CourseClassDTO courseClass;

    @BeforeEach
    void setUp() {
        // A sample Registration entity
        reg = new Registration();
        reg.setRegistrationId(1L);
        reg.setStudentId(42L);
        reg.setClassId(100L);
        reg.setRegisteredAt(LocalDateTime.of(2025, 5,  9, 14, 30));
        reg.setRegistrationStatus("Pending");
        reg.setGroupRegistrationId(null);

        // A sample CourseClassDTO
        courseClass = new CourseClassDTO();
        courseClass.setClassId(100L);
        courseClass.setVacancy(3);
    }

    @Test
    void supports_whenIdentifierIsOne_returnsTrue() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setIdentifier(1);

        assertTrue(strategy.supports(dto));
    }

    @Test
    void supports_whenIdentifierIsNotOne_returnsFalse() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setIdentifier(2);

        assertFalse(strategy.supports(dto));
    }

    @Test
    void update_whenRegistrationNotFound_throws() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setIdentifier(1);
        dto.setId(99L);

        when(repo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> strategy.update(dto));
        assertEquals("Registration not found: 99", ex.getMessage());

        verify(repo).findById(99L);
        verifyNoMoreInteractions(repo, client, waitlistNotificationService);
    }

    @Test
    void update_whenNoStatusChange_returnsExistingDTOOnly() {
        // old = "Pending", new = "Pending"
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setIdentifier(1);
        dto.setId(1L);
        dto.setNewStatus("Pending");

        when(repo.findById(1L)).thenReturn(Optional.of(reg));
        when(client.fetchClass(reg.getClassId()))
            .thenReturn(courseClass);   // <-- stub fetchClass

        List<RegistrationDTO> out = strategy.update(dto);

        // result is still the same
        assertEquals(1, out.size());
        RegistrationDTO r = out.get(0);
        assertEquals(1L,       r.getRegistrationId());
        assertEquals("Pending", r.getRegistrationStatus());

        // verify exactly one client interaction—and nothing else
        verify(client).fetchClass(reg.getClassId());
        verifyNoMoreInteractions(client);

        // repo should not save anything new
        verify(repo).findById(1L);
        verify(repo, never()).save(any());
    }


    @Test
    void update_toRegistered_whenVacancyZero_throws() {
        reg.setRegistrationStatus("Waitlisted");
        courseClass.setVacancy(0);

        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setIdentifier(1);
        dto.setId(1L);
        dto.setNewStatus("Registered");

        when(repo.findById(1L)).thenReturn(Optional.of(reg));
        when(client.fetchClass(100L)).thenReturn(courseClass);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> strategy.update(dto));
        assertTrue(ex.getMessage().contains("No vacancy available for class 100"));

        verify(client).fetchClass(100L);
        verify(client, never()).updateVacancy(any(), anyInt());
        verify(repo, never()).save(any());
    }

    @Test
    void update_toRegistered_successfullyDecrementsVacancy_andSaves() {
        // from "Pending" → "Registered"
        reg.setRegistrationStatus("Waitlisted");
        courseClass.setVacancy(5);

        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setIdentifier(1);
        dto.setId(1L);
        dto.setNewStatus("Registered");

        when(repo.findById(1L)).thenReturn(Optional.of(reg));
        when(client.fetchClass(100L)).thenReturn(courseClass);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        List<RegistrationDTO> out = strategy.update(dto);

        // verify vacancy update
        verify(client).updateVacancy(courseClass, 4);

        // verify entity saved with new status
        ArgumentCaptor<Registration> saved = ArgumentCaptor.forClass(Registration.class);
        verify(repo).save(saved.capture());
        assertEquals("Registered", saved.getValue().getRegistrationStatus());

        // result DTO
        assertEquals(1, out.size());
        assertEquals("Registered", out.get(0).getRegistrationStatus());
    }

    @Test
    void update_fromRegistered_incrementsVacancy_notifiesWaitlist_andSaves() {
        // from "Registered" → "Cancelled"
        reg.setRegistrationStatus("Registered");
        courseClass.setVacancy(2);

        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setIdentifier(1);
        dto.setId(1L);
        dto.setNewStatus("Cancelled");

        when(repo.findById(1L)).thenReturn(Optional.of(reg));
        when(client.fetchClass(100L)).thenReturn(courseClass);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        List<RegistrationDTO> out = strategy.update(dto);

        // vacancy should go from 2 → 3
        verify(client).updateVacancy(courseClass, 3);

        // waitlist service should be invoked
        verify(waitlistNotificationService).notifyWaitlistedStudents(100L, courseClass);

        // verify save
        ArgumentCaptor<Registration> saved = ArgumentCaptor.forClass(Registration.class);
        verify(repo).save(saved.capture());
        assertEquals("Cancelled", saved.getValue().getRegistrationStatus());

        assertEquals(1, out.size());
        assertEquals("Cancelled", out.get(0).getRegistrationStatus());
    }
}
