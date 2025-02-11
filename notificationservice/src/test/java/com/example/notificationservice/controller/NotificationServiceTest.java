package com.example.notificationservice.controller;

import com.example.notificationservice.model.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(NotificationController.class) 
public class NotificationServiceTest {

    @Autowired
    private MockMvc mockMvc;
/*
    // Test for /getNotification endpoint
    @Test
    public void testGetNotification() throws Exception {
        Notification notification = new Notification("Notification test", "Testing notification");

        mockMvc.perform(get("/api/notification/getNotification")
                .param("notificationName", notification.getNotificationName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Getting notification details for: " + notification.getNotificationName()));
    }
*/

}
