package com.example.courseregistration.service;

import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.StudentDTO;
import com.example.courseregistration.model.Registration;
import com.example.courseregistration.repository.CourseRegistrationRepository;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitlistNotificationServiceTest {

    @Mock
    private CourseRegistrationRepository repo;

    @Mock
    private MicroserviceClient client;

    @Mock
    private NotificationPublisherService publisher;

    @InjectMocks
    private WaitlistNotificationService service;

    private final Long classId = 123L;
    private CourseClassDTO courseClass;

    @BeforeEach
    void setUp() {
        courseClass = new CourseClassDTO();
        courseClass.setClassId(classId);
        // other fields as needed...
    }

    @Test
    void notifyWaitlistedStudents_whenNoOneWaitlisted_doesNothing() {
        when(repo.filterRegistration(
                /* studentId */   isNull(),
                /* groupRegId */  isNull(),
                eq(classId),
                eq("Waitlisted"),
                isNull()))
            .thenReturn(Collections.emptyList());

        service.notifyWaitlistedStudents(classId, courseClass);

        // no interactions with client or publisher
        verify(repo).filterRegistration(null, null, classId, "Waitlisted", null);
        verifyNoInteractions(client, publisher);
    }

    @Test
    void notifyWaitlistedStudents_sendsToEachAndContinuesOnError() {
        // prepare two waitlisted registrations
        Registration r1 = new Registration(); r1.setStudentId(1L);
        Registration r2 = new Registration(); r2.setStudentId(2L);
        List<Registration> waitlisted = Arrays.asList(r1, r2);

        when(repo.filterRegistration(null, null, classId, "Waitlisted", null))
            .thenReturn(waitlisted);

        // stub fetchStudentById
        StudentDTO s1 = new StudentDTO(); s1.setStudentFullId("FULL1");
        StudentDTO s2 = new StudentDTO(); s2.setStudentFullId("FULL2");
        when(client.fetchStudentById(1L)).thenReturn(s1);
        when(client.fetchStudentById(2L)).thenReturn(s2);

        // make first publish call throw
        doThrow(new RuntimeException("publish-fail"))
            .when(publisher).publishVacancyAvailableNotification(eq("FULL1"), eq(1L), eq(courseClass));

        // act — should not propagate
        assertDoesNotThrow(() ->
            service.notifyWaitlistedStudents(classId, courseClass)
        );

        // verify both were attempted
        InOrder inOrder = inOrder(client, publisher);
        inOrder.verify(client).fetchStudentById(1L);
        inOrder.verify(publisher).publishVacancyAvailableNotification("FULL1", 1L, courseClass);
        inOrder.verify(client).fetchStudentById(2L);
        inOrder.verify(publisher).publishVacancyAvailableNotification("FULL2", 2L, courseClass);

        verifyNoMoreInteractions(client, publisher);
    }

    @Test
    void notifyStudent_withFullId_usesFullIdOverload() {
        StudentDTO student = new StudentDTO();
        student.setStudentFullId("ABC123");
        when(client.fetchStudentById(99L)).thenReturn(student);

        service.notifyStudent(99L, courseClass);

        verify(client).fetchStudentById(99L);
        verify(publisher).publishVacancyAvailableNotification("ABC123", 99L, courseClass);
        verifyNoMoreInteractions(client, publisher);
    }

    @Test
    void notifyStudent_withoutFullId_usesNumericOverload() {
        StudentDTO student = new StudentDTO();
        student.setStudentFullId(null);
        when(client.fetchStudentById(77L)).thenReturn(student);

        service.notifyStudent(77L, courseClass);

        verify(client).fetchStudentById(77L);
        verify(publisher).publishVacancyAvailableNotification(77L, courseClass);
        verifyNoMoreInteractions(client, publisher);
    }

    @Test
    void notifyStudent_whenFetchFails_throws() {
        when(client.fetchStudentById(5L))
            .thenThrow(new IllegalStateException("downstream error"));

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> service.notifyStudent(5L, courseClass)
        );
        assertEquals("downstream error", ex.getMessage());

        verify(client).fetchStudentById(5L);
        verifyNoMoreInteractions(publisher);
    }

    @Test
    void notifyStudent_whenPublishFails_throws() {
        StudentDTO student = new StudentDTO();
        student.setStudentFullId("ZXY");
        when(client.fetchStudentById(3L)).thenReturn(student);
        doThrow(new RuntimeException("publish oops"))
            .when(publisher).publishVacancyAvailableNotification("ZXY", 3L, courseClass);

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> service.notifyStudent(3L, courseClass)
        );
        assertEquals("publish oops", ex.getMessage());

        verify(client).fetchStudentById(3L);
        verify(publisher).publishVacancyAvailableNotification("ZXY", 3L, courseClass);
    }
}
