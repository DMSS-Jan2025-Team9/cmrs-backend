package com.example.courseregistration.service;

import com.example.courseregistration.dto.CreateRegistrationDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.UpdateRegistrationStatusDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import com.example.courseregistration.service.strategy.RegistrationCreationStrategy;
import com.example.courseregistration.service.strategy.RegistrationStatusUpdateStrategy;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock
    private CourseRegistrationRepository repository;

    // Use real lists instantiated in setUp, not mocks
    private List<RegistrationCreationStrategy> creationStrategies;

    private List<RegistrationStatusUpdateStrategy> statusUpdateStrategies;

    @Mock
    private MicroserviceClient microserviceClient;

    @Mock
    private NotificationPublisherService notificationPublisherService;

    @Mock
    private WaitlistNotificationService waitlistNotificationService;

    @InjectMocks
    private CourseRegistrationService service;

    private Registration reg1;
    private Registration reg2;

    @BeforeEach
    void setUp() {
        // Initialize strategies lists as mutable
        creationStrategies = mock(List.class);
        statusUpdateStrategies = mock(List.class);
        // Inject these mocks manually
        service = new CourseRegistrationService(repository, creationStrategies, statusUpdateStrategies,
                microserviceClient, notificationPublisherService, waitlistNotificationService);

        reg1 = new Registration();
        reg1.setRegistrationId(1L);
        reg1.setStudentId(10L);
        reg1.setClassId(100L);
        reg1.setRegisteredAt(LocalDateTime.now());
        reg1.setRegistrationStatus("Registered");
        reg1.setGroupRegistrationId(null);

        reg2 = new Registration();
        reg2.setRegistrationId(2L);
        reg2.setStudentId(20L);
        reg2.setClassId(200L);
        reg2.setRegisteredAt(LocalDateTime.now());
        reg2.setRegistrationStatus("Unenrolled");
        reg2.setGroupRegistrationId(5L);
    }

    @Test
    void testFilterRegistration_noGroupFilter() {
        when(repository.filterRegistration(null, null, null, null, null))
                .thenReturn(Arrays.asList(reg1, reg2));

        List<RegistrationDTO> results = service.filterRegistration(null, null, null, null, null, null);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.getRegistrationId().equals(1L)));
        assertTrue(results.stream().anyMatch(r -> r.getRegistrationId().equals(2L)));
        verify(repository).filterRegistration(null, null, null, null, null);
    }

    @Test
    void testFilterRegistration_groupTrue() {
        when(repository.filterRegistration(null, null, null, null, null))
                .thenReturn(Arrays.asList(reg1, reg2));

        List<RegistrationDTO> results = service.filterRegistration(null, null, null, null, null, true);

        assertEquals(1, results.size());
        assertEquals(2L, results.get(0).getRegistrationId());
    }

    @Test
    void testFilterRegistration_groupFalse() {
        when(repository.filterRegistration(null, null, null, null, null))
                .thenReturn(Arrays.asList(reg1, reg2));

        List<RegistrationDTO> results = service.filterRegistration(null, null, null, null, null, false);

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getRegistrationId());
    }

    @Test
    void testCreateRegistration_success() {
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        RegistrationCreationStrategy strategy = mock(RegistrationCreationStrategy.class);
        RegistrationDTO out = new RegistrationDTO(1L, 10L, 100L, LocalDateTime.now(), "Registered", null);

        // Stub list.stream() via doReturn for default method
        doReturn(Stream.of(strategy)).when(creationStrategies).stream();
        when(strategy.supports(dto)).thenReturn(true);
        when(strategy.create(dto)).thenReturn(Collections.singletonList(out));

        List<RegistrationDTO> result = service.createRegistration(dto);

        assertEquals(1, result.size());
        assertEquals(out, result.get(0));
    }

    @Test
    void testCreateRegistration_unsupported() {
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        doReturn(Stream.empty()).when(creationStrategies).stream();

        assertThrows(IllegalArgumentException.class, () -> service.createRegistration(dto));
    }

    @Test
    void testUpdateRegistrationStatus_success() {
        UpdateRegistrationStatusDTO dto = mock(UpdateRegistrationStatusDTO.class);
        RegistrationStatusUpdateStrategy strategy = mock(RegistrationStatusUpdateStrategy.class);
        RegistrationDTO out = new RegistrationDTO(1L, 10L, 100L, LocalDateTime.now(), "Waitlisted", null);

        doReturn(Stream.of(strategy)).when(statusUpdateStrategies).stream();
        when(strategy.supports(dto)).thenReturn(true);
        when(strategy.update(dto)).thenReturn(Collections.singletonList(out));

        List<RegistrationDTO> result = service.updateRegistrationStatus(dto);

        assertEquals(1, result.size());
        assertEquals(out, result.get(0));
    }

    @Test
    void testUpdateRegistrationStatus_unsupported() {
        UpdateRegistrationStatusDTO dto = mock(UpdateRegistrationStatusDTO.class);
        doReturn(Stream.empty()).when(statusUpdateStrategies).stream();

        assertThrows(IllegalArgumentException.class, () -> service.updateRegistrationStatus(dto));
    }

    @Test
    void testUnenrollRegistration_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.unenrollRegistration(1L));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testUnenrollRegistration_alreadyUnenrolled() {
        reg2.setRegistrationStatus("Unenrolled");
        when(repository.findById(2L)).thenReturn(Optional.of(reg2));

        RegistrationDTO dto = service.unenrollRegistration(2L);
        assertEquals("Unenrolled", dto.getRegistrationStatus());
        verify(repository, never()).save(any());
    }

    @Test
    void testUnenrollRegistration_registered() {
        reg1.setRegistrationStatus("Registered");
        when(repository.findById(1L)).thenReturn(Optional.of(reg1));
        CourseClassDTO cls = new CourseClassDTO();
        cls.setVacancy(0);
        when(microserviceClient.fetchClass(100L)).thenReturn(cls);
        when(repository.save(any(Registration.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistrationDTO dto = service.unenrollRegistration(1L);

        assertEquals("Unenrolled", dto.getRegistrationStatus());
        ArgumentCaptor<Integer> vacCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(microserviceClient).updateVacancy(eq(cls), vacCaptor.capture());
        assertEquals(1, vacCaptor.getValue());
        verify(waitlistNotificationService).notifyWaitlistedStudents(100L, cls);
        verify(repository).save(any(Registration.class));
    }
}
