package com.example.notificationservice.controller;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // Get all notifications
    @GetMapping
    public List<Notification> getNotifications() {
        return notificationRepository.findAll();
    }

    // Get notification by ID
    @GetMapping("/{id}")
    public Notification getNotification(@PathVariable Long id) {
        return notificationRepository.findById(id).orElse(null);  // Returns null if not found
    }

    // Create a new notification
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationRepository.save(notification);
    }

    // Delete notification by ID
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationRepository.deleteById(id);
    }
}
