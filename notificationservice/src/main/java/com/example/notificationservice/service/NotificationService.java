package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationEventDTO;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Notification createNotification(NotificationEventDTO eventDTO) {
        logger.debug("Creating notification from event: {}", eventDTO);

        if (eventDTO == null) {
            logger.error("NotificationEventDTO is null");
            throw new IllegalArgumentException("NotificationEventDTO cannot be null");
        }

        if (eventDTO.getStudentFullId() == null) {
            logger.error("StudentFullId is null in NotificationEventDTO");
            throw new IllegalArgumentException("StudentFullId cannot be null");
        }

        logger.debug("StudentFullId: {}, Message: {}", eventDTO.getStudentFullId(), eventDTO.getMessage());

        Notification notification = new Notification();
        notification.setStudentFullId(eventDTO.getStudentFullId());

        // For backward compatibility, also set userId if available
        if (eventDTO.getStudentId() != null) {
            notification.setUserId(eventDTO.getStudentId());
        }

        notification.setNotificationMessage(eventDTO.getMessage());
        notification.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Notification savedNotification = notificationRepository.save(notification);
        logger.debug("Saved notification: {}", savedNotification);
        return savedNotification;
    }

    public void sendNotification(Notification notification) {
        // Set the sentAt timestamp
        notification.setSentAt(new Timestamp(System.currentTimeMillis()));
        notificationRepository.save(notification);

        // Send notification to the specific user topic - use studentFullId if
        // available, otherwise fall back to userId
        String userIdentifier = notification.getStudentFullId() != null ? notification.getStudentFullId()
                : notification.getUserId().toString();

        messagingTemplate.convertAndSend(
                "/topic/user/" + userIdentifier,
                notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUserNotificationsByFullId(String studentFullId) {
        return notificationRepository.findByStudentFullIdOrderByCreatedAtDesc(studentFullId);
    }

    public void markAsSent(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setSentAt(new Timestamp(System.currentTimeMillis()));
            notificationRepository.save(notification);
        });
    }

    /**
     * Marks a notification as read by setting the readAt timestamp
     * 
     * @param notificationId The ID of the notification to mark as read
     * @return The updated notification or null if not found
     */
    public Notification markAsRead(Long notificationId) {
        logger.debug("Marking notification as read: {}", notificationId);

        return notificationRepository.findById(notificationId).map(notification -> {
            // Only mark as read if not already read
            if (notification.getReadAt() == null) {
                notification.setReadAt(new Timestamp(System.currentTimeMillis()));
                Notification updatedNotification = notificationRepository.save(notification);
                logger.debug("Notification marked as read: {}", updatedNotification);
                return updatedNotification;
            }
            logger.debug("Notification was already read: {}", notification);
            return notification;
        }).orElse(null);
    }

    /**
     * Marks multiple notifications as read
     * 
     * @param notificationIds List of notification IDs to mark as read
     * @return List of updated notifications
     */
    public List<Notification> markMultipleAsRead(List<Long> notificationIds) {
        logger.debug("Marking multiple notifications as read: {}", notificationIds);

        if (notificationIds == null || notificationIds.isEmpty()) {
            logger.warn("Empty notification ID list provided");
            return Collections.emptyList();
        }

        List<Notification> updatedNotifications = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (Long id : notificationIds) {
            notificationRepository.findById(id).ifPresent(notification -> {
                if (notification.getReadAt() == null) {
                    notification.setReadAt(now);
                    updatedNotifications.add(notificationRepository.save(notification));
                } else {
                    updatedNotifications.add(notification);
                }
            });
        }

        logger.debug("Marked {} notifications as read", updatedNotifications.size());
        return updatedNotifications;
    }

    // /**
    //  * Creates a notification and sends it immediately via WebSocket
    //  * Used for manual testing
    //  * 
    //  * @param notification The notification to create and send
    //  * @return The saved notification
    //  */
    // public Notification createAndSendManualNotification(Notification notification) {
    //     // Save the notification to the database
    //     Notification savedNotification = notificationRepository.save(notification);

    //     // Send it via WebSocket
    //     sendNotification(savedNotification);

    //     return savedNotification;
    // }
}