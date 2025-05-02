package com.example.courseregistration.service;

import com.example.courseregistration.config.RabbitMQConfig;
import com.example.courseregistration.dto.CourseClassDTO;
import com.example.courseregistration.dto.NotificationEventDTO;
import com.example.courseregistration.service.client.MicroserviceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationPublisherServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MicroserviceClient microserviceClient;

    @InjectMocks
    private NotificationPublisherService publisherService;

    private CourseClassDTO courseClass;
    private Long studentId;

    @BeforeEach
    public void setup() {
        studentId = 1L;

        courseClass = new CourseClassDTO();
        courseClass.setClassId(101L);
        courseClass.setCourseId(201L);
        courseClass.setVacancy(0);
    }

    @Test
    public void testPublishWaitlistNotification() {
        publisherService.publishWaitlistNotification(studentId, courseClass);

        ArgumentCaptor<NotificationEventDTO> eventCaptor = ArgumentCaptor.forClass(NotificationEventDTO.class);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NAME),
                eq(RabbitMQConfig.WAITLIST_ROUTING_KEY),
                eventCaptor.capture());

        NotificationEventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(studentId, capturedEvent.getStudentId());
        assertEquals(courseClass.getClassId(), capturedEvent.getClassId());
        assertEquals("WAITLISTED", capturedEvent.getEventType());
    }

    @Test
    public void testPublishVacancyAvailableNotification() {
        publisherService.publishVacancyAvailableNotification(studentId, courseClass);

        ArgumentCaptor<NotificationEventDTO> eventCaptor = ArgumentCaptor.forClass(NotificationEventDTO.class);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NAME),
                eq(RabbitMQConfig.VACANCY_ROUTING_KEY),
                eventCaptor.capture());

        NotificationEventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(studentId, capturedEvent.getStudentId());
        assertEquals(courseClass.getClassId(), capturedEvent.getClassId());
        assertEquals("VACANCY_AVAILABLE", capturedEvent.getEventType());
    }
}