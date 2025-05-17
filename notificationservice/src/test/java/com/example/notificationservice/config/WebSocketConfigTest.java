package com.example.notificationservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebSocketConfigTest {

    @InjectMocks
    private WebSocketConfig webSocketConfig;

    @Test
    public void testConfigureMessageBroker() {
        // Create a mock MessageBrokerRegistry
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);

        // Call the method to test
        webSocketConfig.configureMessageBroker(registry);

        // Verify the correct methods were called
        verify(registry).enableSimpleBroker("/topic");
        verify(registry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    public void testRegisterStompEndpoints() {
        // Create mocks
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration registration = mock(StompWebSocketEndpointRegistration.class);

        // Setup mock behavior
        when(registry.addEndpoint(anyString())).thenReturn(registration);
        when(registration.setAllowedOriginPatterns(anyString())).thenReturn(registration);

        // Call the method to test
        webSocketConfig.registerStompEndpoints(registry);

        // Basic verification
        verify(registry).addEndpoint("/ws-notifications");
    }
}