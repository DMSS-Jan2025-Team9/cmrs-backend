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

        if (eventDTO.getStudentId() == null) {
            logger.error("StudentId is null in NotificationEventDTO");
            throw new IllegalArgumentException("StudentId cannot be null");
        }

        logger.debug("StudentId: {}, Message: {}", eventDTO.getStudentId(), eventDTO.getMessage());

        Notification notification = new Notification();
        notification.setUserId(eventDTO.getStudentId());
        notification.setNotificationMessage(eventDTO.getMessage());
        notification.setCreatedAt(Timestamp.from(Instant.now()));

        Notification savedNotification = notificationRepository.save(notification);
        logger.debug("Saved notification: {}", savedNotification);
        return savedNotification;
    }

    public void sendNotification(Notification notification) {
        // Set the sentAt timestamp
        notification.setSentAt(Timestamp.from(Instant.now()));
        notificationRepository.save(notification);

        // Send notification to the specific user topic
        messagingTemplate.convertAndSend(
                "/topic/user/" + notification.getUserId(),
                notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsSent(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setSentAt(Timestamp.from(Instant.now()));
            notificationRepository.save(notification);
        });
    }

    /**
     * Creates a notification and sends it immediately via WebSocket
     * Used for manual testing
     * 
     * @param notification The notification to create and send
     * @return The saved notification
     */
    public Notification createAndSendManualNotification(Notification notification) {
        // Save the notification to the database
        Notification savedNotification = notificationRepository.save(notification);

        // Send it via WebSocket
        sendNotification(savedNotification);

        return savedNotification;
    }
}