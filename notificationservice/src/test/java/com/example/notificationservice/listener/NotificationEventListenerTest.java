package com.example.notificationservice.listener;

import com.example.notificationservice.dto.NotificationEventDTO;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationEventListener eventListener;

    private NotificationEventDTO eventDTO;
    private Notification notification;

    @BeforeEach
    public void setup() {
        eventDTO = new NotificationEventDTO();
        eventDTO.setStudentId(1L);
        eventDTO.setStudentFullId("U7656");
        eventDTO.setClassId(101L);
        eventDTO.setCourseCode("CS101");
        eventDTO.setCourseName("Introduction to Computer Science");
        eventDTO.setMessage("Test notification message");
        eventDTO.setEventType("WAITLISTED");

        notification = new Notification();
        notification.setNotificationId(1L);
        notification.setUserId(1L);
        notification.setNotificationMessage("Test notification message");
    }

    @Test
    public void testHandleNotificationEvent() {
        when(notificationService.createNotification(any(NotificationEventDTO.class))).thenReturn(notification);

        eventListener.handleNotificationEvent(eventDTO);

        verify(notificationService, times(1)).createNotification(eventDTO);
        verify(notificationService, times(1)).sendNotification(notification);
    }
}