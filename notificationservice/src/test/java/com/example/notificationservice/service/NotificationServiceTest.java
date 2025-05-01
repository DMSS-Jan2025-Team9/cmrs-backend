package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationEventDTO;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

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
        notification.setCreatedAt(Timestamp.from(Instant.now()));
    }

    @Test
    public void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createNotification(eventDTO);

        assertNotNull(result);
        assertEquals(notification.getNotificationId(), result.getNotificationId());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testSendNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotification(notification);

        assertNotNull(notification.getSentAt());
        verify(notificationRepository, times(1)).save(notification);
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), any(Notification.class));
    }

    @Test
    public void testGetUserNotifications() {
        List<Notification> notificationList = Arrays.asList(notification);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(notificationList);

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertEquals(1, result.size());
        assertEquals(notification.getNotificationId(), result.get(0).getNotificationId());
        verify(notificationRepository, times(1)).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    public void testMarkAsSent() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsSent(1L);

        assertNotNull(notification.getSentAt());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(notification);
    }
}