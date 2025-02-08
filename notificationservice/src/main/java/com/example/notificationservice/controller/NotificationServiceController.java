package com.example.notificationservice.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/notification")
public class NotificationServiceController {

    @GetMapping("/getNotification")
    public String getNotification(@RequestParam String notificationName) {
        return "Getting notification details for: " + notificationName;
    }

    
}

