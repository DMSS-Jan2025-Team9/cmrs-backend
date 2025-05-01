package com.example.notificationservice.repository;

import com.example.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Method to find notifications for a specific user, ordered by creation time
    // descending
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Method to find notifications by student full ID
    List<Notification> findByStudentFullIdOrderByCreatedAtDesc(String studentFullId);

    // You can add custom query methods here if needed
}
