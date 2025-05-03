package com.example.courseregistration.service.strategy;

import com.example.courseregistration.dto.CreateRegistrationDTO;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.RegistrationDTO;
import com.example.courseregistration.dto.StudentDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupRegistrationCreationStrategyTest {

    @Mock
    private CourseRegistrationRepository repository;

    @Mock
    private MicroserviceClient microserviceClient;

    @InjectMocks
    private GroupRegistrationCreationStrategy strategy;

    @BeforeEach
    void setUp() {
        // constructor injection handled by @InjectMocks
    }

    @Test
    void supports_returnsTrueWhenMultipleStudents() {
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        when(dto.getStudentFullIds()).thenReturn(Arrays.asList("s1", "s2"));

        assertTrue(strategy.supports(dto));
    }

    @Test
    void supports_returnsFalseWhenSingleStudent() {
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        when(dto.getStudentFullIds()).thenReturn(Collections.singletonList("s1"));

        assertFalse(strategy.supports(dto));
    }

    @Test
    void create_registersAllWhenVacancySufficient_andGeneratesGroupId1IfNone() {
        // Given
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        List<String> fullIds = Arrays.asList("f1", "f2");
        when(dto.getStudentFullIds()).thenReturn(fullIds);
        when(dto.getClassId()).thenReturn(100L);

        // No existing groups
        when(repository.findMaxGroupRegistrationId()).thenReturn(null);

        // Students exist
        StudentDTO student1 = new StudentDTO();
        student1.setStudentId(11L);
        when(microserviceClient.fetchStudentByFullId("f1")).thenReturn(student1);
        StudentDTO student2 = new StudentDTO();
        student2.setStudentId(22L);
        when(microserviceClient.fetchStudentByFullId("f2")).thenReturn(student2);

        CourseClassDTO course = new CourseClassDTO();
        course.setVacancy(5);
        when(microserviceClient.fetchClass(100L)).thenReturn(course);

        // Capture saves
        when(repository.save(any(Registration.class))).thenAnswer(inv -> {
            Registration reg = inv.getArgument(0);
            reg.setRegistrationId(reg.getStudentId()); // use studentId as regId
            return reg;
        });

        // When
        List<RegistrationDTO> results = strategy.create(dto);

        // Then
        assertEquals(2, results.size());
        ArgumentCaptor<Integer> vacancyCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(microserviceClient).updateVacancy(eq(course), vacancyCaptor.capture());
        assertEquals(5 - 2, vacancyCaptor.getValue());

        // All registrations should have status Registered and groupId=1
        for (RegistrationDTO r : results) {
            assertEquals("Registered", r.getRegistrationStatus());
            assertEquals(100L, r.getClassId());
            assertEquals(1L, r.getGroupRegistrationId());
        }
    }

    @Test
    void create_waitlistsAllWhenVacancyInsufficient_andIncrementsGroupId() {
        // Given
        CreateRegistrationDTO dto = mock(CreateRegistrationDTO.class);
        List<String> fullIds = Arrays.asList("fA", "fB", "fC");
        when(dto.getStudentFullIds()).thenReturn(fullIds);
        when(dto.getClassId()).thenReturn(200L);

        // Existing max group id = 5
        when(repository.findMaxGroupRegistrationId()).thenReturn(5L);

        StudentDTO studentA = new StudentDTO();
        studentA.setStudentId(101L);
        when(microserviceClient.fetchStudentByFullId("fA")).thenReturn(studentA);
        StudentDTO studentB = new StudentDTO();
        studentB.setStudentId(102L);
        when(microserviceClient.fetchStudentByFullId("fB")).thenReturn(studentB);
        StudentDTO studentC = new StudentDTO();
        studentC.setStudentId(103L);
        when(microserviceClient.fetchStudentByFullId("fC")).thenReturn(studentC);

        CourseClassDTO course = new CourseClassDTO();
        course.setVacancy(2);
        when(microserviceClient.fetchClass(200L)).thenReturn(course);

        when(repository.save(any(Registration.class))).thenAnswer(inv -> {
            Registration reg = inv.getArgument(0);
            reg.setRegistrationId(reg.getStudentId());
            return reg;
        });

        // When
        List<RegistrationDTO> results = strategy.create(dto);

        // Then
        assertEquals(3, results.size());
        // No vacancy update since insufficient
        verify(microserviceClient, never()).updateVacancy(any(), anyInt());

        // All registrations should have status Waitlisted and groupId=6
        for (RegistrationDTO r : results) {
            assertEquals("Waitlisted", r.getRegistrationStatus());
            assertEquals(200L, r.getClassId());
            assertEquals(6L, r.getGroupRegistrationId());
        }
    }
}
