package com.example.courseregistration;

import java.util.Map;

import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HealthLogger {
    private static final Logger logger = LoggerFactory.getLogger(HealthLogger.class);

    private final HealthEndpoint healthEndpoint;

    public HealthLogger(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void logHealthAtStartup() {
        HealthComponent health = healthEndpoint.health();
        logHealthComponent("root", health);
    }

    private void logHealthComponent(String name, HealthComponent component) {
        if (component instanceof Health health) {
            // Build the log string for the health check
            StringBuilder healthLog = new StringBuilder("Health Check [" + name + "]: " + health.getStatus());
            for (Map.Entry<String, Object> entry : health.getDetails().entrySet()) {
                healthLog.append(" | ").append(entry.getKey()).append(": ").append(entry.getValue());
            }
            // Log the health check in one line
            logger.info(healthLog.toString());
        } else if (component instanceof CompositeHealth composite) {
            // Log the overall status of the composite health
            StringBuilder compositeLog = new StringBuilder("Composite Health [" + name + "]: " + composite.getStatus());
    
            // Check if the components are iterable
            Object components = composite.getComponents();
            if (components instanceof Iterable<?>) {
                // Cast safely after type check
                Iterable<HealthComponent> iterableComponents = (Iterable<HealthComponent>) components;
                // Iterate over each component if it's iterable
                for (HealthComponent subComponent : iterableComponents) {
                    logHealthComponent(name, subComponent);
                }
            } else {
                // Handle the case where components are not iterable
                logger.warn("Composite Health [" + name + "] components are not iterable: " + components);
            }

            // Log the composite health status in one line
            logger.info(compositeLog.toString());
        }
    }

    @Scheduled(fixedRate = 600000) // every 10 minutes
    public void logHealthStatus() {
        var health = healthEndpoint.health();
        logger.info("Health status: " + health.getStatus());
        logHealthComponent("root", health);
    }
}