package com.example.notificationservice.controller;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationController controller;

    private Notification notification;
    private List<Notification> notifications;

    @BeforeEach
    public void setup() {
        notification = new Notification();
        notification.setNotificationId(1L);
        notification.setUserId(100L);
        notification.setStudentFullId("student123");
        notification.setNotificationMessage("Test notification");
        notification.setCreatedAt(Timestamp.from(Instant.now()));

        notifications = Arrays.asList(notification);
    }

    @Test
    public void testGetUserNotifications() {
        when(notificationService.getUserNotifications(anyLong())).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = controller.getUserNotifications(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getNotificationId());
        verify(notificationService).getUserNotifications(100L);
    }

    @Test
    public void testGetStudentNotificationsByFullId() {
        when(notificationService.getUserNotificationsByFullId(anyString())).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = controller.getStudentNotificationsByFullId("student123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getNotificationId());
        verify(notificationService).getUserNotificationsByFullId("student123");
    }

    @Test
    public void testMarkNotificationAsSent() {
        doNothing().when(notificationService).markAsSent(anyLong());

        ResponseEntity<Void> response = controller.markNotificationAsSent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService).markAsSent(1L);
    }

    @Test
    public void testMarkAsRead() {
        when(notificationService.markAsRead(anyLong())).thenReturn(notification);

        ResponseEntity<Notification> response = controller.markAsRead(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getNotificationId());
        verify(notificationService).markAsRead(1L);
    }

    @Test
    public void testMarkAsReadNotFound() {
        when(notificationService.markAsRead(anyLong())).thenReturn(null);

        ResponseEntity<Notification> response = controller.markAsRead(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(notificationService).markAsRead(999L);
    }

    @Test
    public void testMarkMultipleAsRead() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(notificationService.markMultipleAsRead(any())).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = controller.markMultipleAsRead(ids);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(notificationService).markMultipleAsRead(ids);
    }

    @Test
    public void testMarkMultipleAsReadWithEmptyList() {
        List<Long> emptyIds = Collections.emptyList();

        ResponseEntity<List<Notification>> response = controller.markMultipleAsRead(emptyIds);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(notificationService, never()).markMultipleAsRead(anyList());
    }
}