package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationEventDTO;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(NotificationService notificationService,
            SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/mark-as-sent")
    public ResponseEntity<Void> markNotificationAsSent(@PathVariable Long notificationId) {
        notificationService.markAsSent(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * Test endpoint to manually create a notification
     * 
     * @param userId  The user to notify
     * @param message The notification message
     * @return The created notification
     */
    @PostMapping("/createNotification")
    public ResponseEntity<Notification> CreateNotification(
            @RequestParam Long userId,
            @RequestParam String message) {

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setNotificationMessage(message);
        notification.setCreatedAt(Timestamp.from(Instant.now()));

        Notification savedNotification = notificationService.createAndSendManualNotification(notification);
        return ResponseEntity.ok(savedNotification);
    }

    /**
     * Test endpoint to manually send a notification event
     * 
     * @param studentId The student ID
     * @param classId   The class ID
     * @param eventType The type of event (WAITLISTED or VACANCY_AVAILABLE)
     * @return Success status
     */
    @PostMapping("/notificationEvent")
    public ResponseEntity<Map<String, String>> NotificationEvent(
            @RequestParam Long studentId,
            @RequestParam Long classId,
            @RequestParam String eventType) {

        String message;

        if (eventType.equalsIgnoreCase("WAITLISTED")) {
            message = "You have been waitlisted for a course with ID " + classId;
        } else if (eventType.equalsIgnoreCase("VACANCY_AVAILABLE")) {
            message = "A vacancy is now available for course with ID " + classId + ". Please register now!";
        } else {
            message = "Notification test message for course " + classId;
        }

        NotificationEventDTO eventDTO = new NotificationEventDTO(
                studentId,
                classId,
                "TEST-CODE",
                "Test Course",
                message,
                eventType.toUpperCase());

        Notification notification = notificationService.createNotification(eventDTO);
        notificationService.sendNotification(notification);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Test notification sent for student " + studentId + " with event type " + eventType));
    }

    /**
     * Test endpoint to directly send a message via WebSocket
     * 
     * @param userId  The user to send the message to
     * @param message The message content
     * @return Success status
     */
    @PostMapping("/test/websocket")
    public ResponseEntity<Map<String, String>> testWebSocketMessage(
            @RequestParam Long userId,
            @RequestParam String message) {

        messagingTemplate.convertAndSend("/topic/user/" + userId,
                Map.of("message", message, "timestamp", Instant.now().toString()));

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "WebSocket message sent to user " + userId));
    }
}
