package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationEventDTO;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
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

    @GetMapping("/student/{studentFullId}")
    public ResponseEntity<List<Notification>> getStudentNotificationsByFullId(@PathVariable String studentFullId) {
        List<Notification> notifications = notificationService.getUserNotificationsByFullId(studentFullId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/mark-as-sent")
    public ResponseEntity<Void> markNotificationAsSent(@PathVariable Long notificationId) {
        notificationService.markAsSent(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long notificationId) {
        // logger.debug("Marking notification as read: {}", notificationId);
        Notification notification = notificationService.markAsRead(notificationId);

        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(notification);
    }

    /**
     * Mark multiple notifications as read
     */
    @PutMapping("/read")
    public ResponseEntity<List<Notification>> markMultipleAsRead(@RequestBody List<Long> notificationIds) {
        // logger.debug("Marking multiple notifications as read: {}", notificationIds);

        if (notificationIds == null || notificationIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Notification> updatedNotifications = notificationService.markMultipleAsRead(notificationIds);
        return ResponseEntity.ok(updatedNotifications);
    }

    // /**
    //  * Test endpoint to manually create a notification
    //  * 
    //  * @param studentFullId The student full ID to notify
    //  * @param message       The notification message
    //  * @return The created notification
    //  */
    // @PostMapping("/createNotification")
    // public ResponseEntity<Notification> CreateNotification(
    //         @RequestParam(required = false) Long userId,
    //         @RequestParam(required = false) String studentFullId,
    //         @RequestParam String message) {

    //     Notification notification = new Notification();
    //     if (studentFullId != null) {
    //         notification.setStudentFullId(studentFullId);
    //     }
    //     if (userId != null) {
    //         notification.setUserId(userId);
    //     }
    //     notification.setNotificationMessage(message);
    //     notification.setCreatedAt(Timestamp.from(Instant.now()));

    //     Notification savedNotification = notificationService.createAndSendManualNotification(notification);
    //     return ResponseEntity.ok(savedNotification);
    // }

    // /**
    // * Test endpoint to manually send a notification event
    // *
    // * @param studentFullId The student full ID
    // * @param studentId The student ID (for backward compatibility)
    // * @param classId The class ID
    // * @param courseCode The course code
    // * @param courseName The course name
    // * @param eventType The type of event (WAITLISTED or VACANCY_AVAILABLE)
    // * @return Success status
    // */
    // @PostMapping("/notificationEvent")
    // public ResponseEntity<Map<String, String>> NotificationEvent(
    // @RequestParam(required = false) Long studentId,
    // @RequestParam(required = false) String studentFullId,
    // @RequestParam Long classId,
    // @RequestParam(required = false) String courseCode,
    // @RequestParam(required = false) String courseName,
    // @RequestParam String eventType) {

    // String message;

    // // Default course information if not provided
    // String finalCourseCode = courseCode != null ? courseCode : "CS101";
    // String finalCourseName = courseName != null ? courseName : "Introduction to
    // Programming";

    // if (studentFullId == null && studentId == null) {
    // return ResponseEntity.badRequest().body(Map.of(
    // "status", "error",
    // "message", "Either studentId or studentFullId must be provided"));
    // }

    // if (eventType.equalsIgnoreCase("WAITLISTED")) {
    // message = "You have been waitlisted for " + finalCourseCode + " - " +
    // finalCourseName;
    // } else if (eventType.equalsIgnoreCase("VACANCY_AVAILABLE")) {
    // message = "A vacancy is now available for " + finalCourseCode + " - " +
    // finalCourseName
    // + ". Please register now!";
    // } else {
    // message = "Notification test message for " + finalCourseCode + " - " +
    // finalCourseName;
    // }

    // NotificationEventDTO eventDTO = new NotificationEventDTO(
    // studentFullId,
    // studentId,
    // classId,
    // finalCourseCode,
    // finalCourseName,
    // message,
    // eventType.toUpperCase());

    // Notification notification = notificationService.createNotification(eventDTO);
    // notificationService.sendNotification(notification);

    // String studentIdentifier = studentFullId != null ? studentFullId :
    // studentId.toString();

    // return ResponseEntity.ok(Map.of(
    // "status", "success",
    // "message",
    // "Test notification sent for student " + studentIdentifier + " with event type
    // " + eventType));
    // }

    // /**
    // * Test endpoint to directly send a message via WebSocket
    // *
    // * @param userId The user to send the message to
    // * @param message The message content
    // * @return Success status
    // */
    // @PostMapping("/websocket-message")
    // public ResponseEntity<Map<String, String>> sendWebSocketMessage(
    // @RequestParam(required = false) Long userId,
    // @RequestParam(required = false) String studentFullId,
    // @RequestParam String message) {

    // if (studentFullId == null && userId == null) {
    // return ResponseEntity.badRequest().body(Map.of(
    // "status", "error",
    // "message", "Either userId or studentFullId must be provided"));
    // }

    // String userIdentifier = studentFullId != null ? studentFullId :
    // userId.toString();

    // // Create a notification object
    // Notification notification = new Notification();
    // if (studentFullId != null)
    // notification.setStudentFullId(studentFullId);
    // if (userId != null)
    // notification.setUserId(userId);
    // notification.setNotificationMessage(message);
    // notification.setCreatedAt(Timestamp.from(Instant.now()));
    // notification.setSentAt(Timestamp.from(Instant.now()));

    // // Send directly without saving to DB
    // messagingTemplate.convertAndSend("/topic/user/" + userIdentifier,
    // notification);

    // return ResponseEntity.ok(Map.of(
    // "status", "success",
    // "message", "WebSocket message sent to user " + userIdentifier));
    // }
}
