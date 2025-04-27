package com.example.notificationservice.listener;

import com.example.notificationservice.config.RabbitMQConfig;
import com.example.notificationservice.dto.NotificationEventDTO;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationEventListener {
    private static final Logger logger = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.REGISTRATION_QUEUE)
    public void handleNotificationEvent(NotificationEventDTO eventDTO) {
        logger.info("Received notification event: {}", eventDTO);

        if (eventDTO == null) {
            logger.error("Received null event DTO");
            return;
        }

        try {
            // Create a notification
            logger.debug("Creating notification from event for studentId: {}", eventDTO.getStudentId());
            Notification notification = notificationService.createNotification(eventDTO);

            // Send the notification via WebSocket
            logger.debug("Sending notification: {}", notification);
            notificationService.sendNotification(notification);
            logger.info("Successfully processed notification for studentId: {}", eventDTO.getStudentId());
        } catch (Exception e) {
            logger.error("Error processing notification event: {}", e.getMessage(), e);
        }
    }
}