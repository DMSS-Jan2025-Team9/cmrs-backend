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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        notification.setStudentFullId("U7656");
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
    public void testCreateNotificationWithNullEventDTO() {
        NotificationEventDTO nullDto = null;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.createNotification(nullDto);
        });

        assertEquals("NotificationEventDTO cannot be null", exception.getMessage());
    }

    @Test
    public void testCreateNotificationWithNullStudentFullId() {
        eventDTO.setStudentFullId(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.createNotification(eventDTO);
        });

        assertEquals("StudentFullId cannot be null", exception.getMessage());
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
    public void testSendNotificationUsesStudentFullId() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotification(notification);

        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/user/U7656"), any(Notification.class));
    }

    @Test
    public void testSendNotificationFallsBackToUserId() {
        notification.setStudentFullId(null);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotification(notification);

        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/user/1"), any(Notification.class));
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
    public void testGetUserNotificationsByFullId() {
        List<Notification> notificationList = Arrays.asList(notification);
        when(notificationRepository.findByStudentFullIdOrderByCreatedAtDesc(anyString())).thenReturn(notificationList);

        List<Notification> result = notificationService.getUserNotificationsByFullId("U7656");

        assertEquals(1, result.size());
        assertEquals(notification.getNotificationId(), result.get(0).getNotificationId());
        verify(notificationRepository, times(1)).findByStudentFullIdOrderByCreatedAtDesc("U7656");
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

    @Test
    public void testMarkAsSentWithNonExistentId() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

        notificationService.markAsSent(999L);

        verify(notificationRepository, times(1)).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testMarkAsRead() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.markAsRead(1L);

        assertNotNull(result);
        assertNotNull(result.getReadAt());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    public void testMarkAsReadAlreadyRead() {
        notification.setReadAt(Timestamp.from(Instant.now()));
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));

        Notification result = notificationService.markAsRead(1L);

        assertNotNull(result);
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testMarkAsReadNonExistentId() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

        Notification result = notificationService.markAsRead(999L);

        assertNull(result);
        verify(notificationRepository, times(1)).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testMarkMultipleAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        Notification notification2 = new Notification();
        notification2.setNotificationId(2L);
        when(notificationRepository.findById(2L)).thenReturn(Optional.of(notification2));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Notification> result = notificationService.markMultipleAsRead(Arrays.asList(1L, 2L));

        assertEquals(2, result.size());
        assertNotNull(result.get(0).getReadAt());
        assertNotNull(result.get(1).getReadAt());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).findById(2L);
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    public void testMarkMultipleAsReadWithAlreadyReadNotification() {
        notification.setReadAt(Timestamp.from(Instant.now()));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Notification notification2 = new Notification();
        notification2.setNotificationId(2L);
        when(notificationRepository.findById(2L)).thenReturn(Optional.of(notification2));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Notification> result = notificationService.markMultipleAsRead(Arrays.asList(1L, 2L));

        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).findById(2L);
        verify(notificationRepository, times(1)).save(any(Notification.class)); // Only notification2 should be saved
    }

    @Test
    public void testMarkMultipleAsReadWithEmptyList() {
        List<Notification> result = notificationService.markMultipleAsRead(Collections.emptyList());

        assertTrue(result.isEmpty());
        verify(notificationRepository, never()).findById(anyLong());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testMarkMultipleAsReadWithNullList() {
        List<Notification> result = notificationService.markMultipleAsRead(null);

        assertTrue(result.isEmpty());
        verify(notificationRepository, never()).findById(anyLong());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    public void testMarkMultipleAsReadWithNonExistentId() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Notification> result = notificationService.markMultipleAsRead(Arrays.asList(1L, 999L));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getNotificationId());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).findById(999L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}