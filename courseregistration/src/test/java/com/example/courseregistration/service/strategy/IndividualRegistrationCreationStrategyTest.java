package com.example.courseregistration.service.strategy;

import com.example.courseregistration.dto.CreateRegistrationDTO;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.StudentDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import com.example.courseregistration.service.NotificationPublisherService;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndividualRegistrationCreationStrategyTest {

    @Mock
    private CourseRegistrationRepository repository;

    @Mock
    private MicroserviceClient microserviceClient;

    @Mock
    private NotificationPublisherService notificationPublisherService;

    @InjectMocks
    private IndividualRegistrationCreationStrategy strategy;

    @BeforeEach
    void setUp() {
        // @InjectMocks handles construction
    }

    @Test
    void supports_returnsTrueForSingleStudent() {
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        when(dto.getStudentFullIds()).thenReturn(Collections.singletonList("stu1"));

        assertTrue(strategy.supports(dto));
    }

    @Test
    void supports_returnsFalseForMultipleStudents() {
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        when(dto.getStudentFullIds()).thenReturn(List.of("stu1", "stu2"));

        assertFalse(strategy.supports(dto));
    }

    @Test
    void create_registersWhenVacancyAvailable() {
        // Given
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        when(dto.getStudentFullIds()).thenReturn(Collections.singletonList("f1"));
        when(dto.getClassId()).thenReturn(50L);

        // Student validation and fetch
        doNothing().when(microserviceClient).validateStudentExists("f1");
        StudentDTO student = new StudentDTO();
        student.setStudentId(123L);
        when(microserviceClient.fetchStudentByFullId("f1")).thenReturn(student);

        // Class with vacancy
        CourseClassDTO course = new CourseClassDTO();
        course.setVacancy(2);
        when(microserviceClient.fetchClass(50L)).thenReturn(course);

        // Stub save to return the passed registration with an ID
        when(repository.save(any(Registration.class))).thenAnswer(inv -> {
            Registration r = inv.getArgument(0);
            r.setRegistrationId(999L);
            return r;
        });

        // When
        List<RegistrationDTO> result = strategy.create(dto);

        // Then
        assertEquals(1, result.size());
        RegistrationDTO dtoOut = result.get(0);
        assertEquals(999L, dtoOut.getRegistrationId());
        assertEquals(123L, dtoOut.getStudentId());
        assertEquals(50L, dtoOut.getClassId());
        assertEquals("Registered", dtoOut.getRegistrationStatus());
        assertNull(dtoOut.getGroupRegistrationId());

        // Vacancy decremented
        ArgumentCaptor<Integer> cap = ArgumentCaptor.forClass(Integer.class);
        verify(microserviceClient).updateVacancy(eq(course), cap.capture());
        assertEquals(1, cap.getValue());

        // No waitlist notification
        verify(notificationPublisherService, never()).publishWaitlistNotification(anyLong(), any());
    }

    @Test
    void create_waitlistsWhenNoVacancy() {
        // Given
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        when(dto.getStudentFullIds()).thenReturn(Collections.singletonList("fX"));
        when(dto.getClassId()).thenReturn(75L);

        doNothing().when(microserviceClient).validateStudentExists("fX");
        StudentDTO student = new StudentDTO();
        student.setStudentId(321L);
        when(microserviceClient.fetchStudentByFullId("fX")).thenReturn(student);

        CourseClassDTO course = new CourseClassDTO();
        course.setVacancy(0);
        when(microserviceClient.fetchClass(75L)).thenReturn(course);

        when(repository.save(any(Registration.class))).thenAnswer(inv -> {
            Registration r = inv.getArgument(0);
            r.setRegistrationId(888L);
            return r;
        });

        // When
        List<RegistrationDTO> result = strategy.create(dto);

        // Then
        assertEquals(1, result.size());
        RegistrationDTO dtoOut = result.get(0);
        assertEquals("Waitlisted", dtoOut.getRegistrationStatus());

        // No vacancy update
        verify(microserviceClient, never()).updateVacancy(any(), anyInt());

        // Waitlist notification sent
        verify(notificationPublisherService).publishWaitlistNotification(eq(321L), eq(course));
    }
}
