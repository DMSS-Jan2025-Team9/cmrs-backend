package com.example.courseregistration.service.strategy;

import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import com.example.courseregistration.service.client.MicroserviceClient;
import com.example.courseregistration.service.NotificationPublisherService;
import com.example.courseregistration.service.WaitlistNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupRegistrationStatusUpdateStrategyTest {

    @Mock
    private CourseRegistrationRepository repo;

    @Mock
    private MicroserviceClient microserviceClient;

    @Mock
    private NotificationPublisherService notificationPublisherService;

    @Mock
    private WaitlistNotificationService waitlistNotificationService;

    @InjectMocks
    private GroupRegistrationStatusUpdateStrategy strategy;

    private Registration reg1;
    private Registration reg2;
    private Registration reg3;
    private CourseClassDTO courseClass;

    @BeforeEach
    void setUp() {
        // common registrations: reg1 = already Registered, reg2 = Waitlisted, reg3 = Registered
        reg1 = new Registration();
        reg1.setRegistrationId(101L);
        reg1.setStudentId(11L);
        reg1.setClassId(1001L);
        reg1.setRegisteredAt(LocalDateTime.now().minusDays(1));
        reg1.setRegistrationStatus("Registered");
        reg1.setGroupRegistrationId(42L);

        reg2 = new Registration();
        reg2.setRegistrationId(102L);
        reg2.setStudentId(12L);
        reg2.setClassId(1001L);
        reg2.setRegisteredAt(LocalDateTime.now().minusHours(5));
        reg2.setRegistrationStatus("Waitlisted");
        reg2.setGroupRegistrationId(42L);

        reg3 = new Registration();
        reg3.setRegistrationId(103L);
        reg3.setStudentId(13L);
        reg3.setClassId(1001L);
        reg3.setRegisteredAt(LocalDateTime.now().minusHours(2));
        reg3.setRegistrationStatus("Registered");
        reg3.setGroupRegistrationId(42L);

        courseClass = new CourseClassDTO();
        courseClass.setClassId(1001L);
    }

    @Test
    void supports_returnsTrue_onlyForIdentifier2() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setIdentifier(2);
        assertTrue(strategy.supports(dto));

        dto.setIdentifier(1);
        assertFalse(strategy.supports(dto));
        dto.setIdentifier(999);
        assertFalse(strategy.supports(dto));
    }

    @Test
    void update_throws_whenNoRegistrationsFound() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setId(42L);
        dto.setNewStatus("Registered");
        dto.setIdentifier(2);

        when(repo.findByGroupRegistrationId(42L)).thenReturn(Collections.emptyList());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> strategy.update(dto));
        assertEquals("Group ID 42 not found", ex.getMessage());
    }

    @Test
    void update_throws_whenInsufficientSeats_forRegistering() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setId(42L);
        dto.setNewStatus("Registered");
        dto.setIdentifier(2);

        // two waitlisted => delta = -2
        List<Registration> regs = Arrays.asList(reg2, reg2);
        when(repo.findByGroupRegistrationId(42L)).thenReturn(regs);

        // course has only 1 vacancy < 2 needed
        courseClass.setVacancy(1);
        when(microserviceClient.fetchClass(1001L)).thenReturn(courseClass);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> strategy.update(dto));
        assertEquals("Not enough seats", ex.getMessage());

        verify(microserviceClient).fetchClass(1001L);
        verify(microserviceClient, never()).updateVacancy(any(), anyInt());
    }

    @Test
    void update_registersSuccessfully_whenVacancySufficient() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setId(42L);
        dto.setNewStatus("Registered");
        dto.setIdentifier(2);

        // one waitlisted, one registered => toRegister=1, toCancel=1 => delta = -1
        List<Registration> regs = Arrays.asList(reg2, reg1);
        when(repo.findByGroupRegistrationId(42L)).thenReturn(regs);

        courseClass.setVacancy(5);
        when(microserviceClient.fetchClass(1001L)).thenReturn(courseClass);

        List<RegistrationDTO> result = strategy.update(dto);

        // verify vacancy updated: new vacancy = 5 + (-1) = 4
        verify(microserviceClient).updateVacancy(courseClass, 4);

        // no waitlist notification on negative delta
        verify(waitlistNotificationService, never()).notifyWaitlistedStudents(anyLong(), any());

        // all registrations should now be "Registered"
        for (Registration r : regs) {
            assertEquals("Registered", r.getRegistrationStatus());
        }
        // repo.saveAll should be called
        verify(repo).saveAll(regs);

        // result should reflect both dtos
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dtoOut -> "Registered".equals(dtoOut.getRegistrationStatus())));
    }

    @Test
    void update_cancelsAndNotifies_whenCancellingGroup() {
        UpdateRegistrationStatusDTO dto = new UpdateRegistrationStatusDTO();
        dto.setId(42L);
        dto.setNewStatus("Cancelled");
        dto.setIdentifier(2);

        // one waitlisted, two registered => toRegister=1, toCancel=2 => delta = +2
        List<Registration> regs = Arrays.asList(reg2, reg1, reg3);
        when(repo.findByGroupRegistrationId(42L)).thenReturn(regs);

        courseClass.setVacancy(3);
        when(microserviceClient.fetchClass(1001L)).thenReturn(courseClass);

        List<RegistrationDTO> result = strategy.update(dto);

        // verify vacancy updated: new vacancy = 3 + 2 = 5
        verify(microserviceClient).updateVacancy(courseClass, 5);

        // waitlist notif should fire once
        verify(waitlistNotificationService).notifyWaitlistedStudents(1001L, courseClass);

        // all registrations now "Cancelled"
        for (Registration r : regs) {
            assertEquals("Cancelled", r.getRegistrationStatus());
        }
        verify(repo).saveAll(regs);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(dtoOut -> "Cancelled".equals(dtoOut.getRegistrationStatus())));
    }
}
