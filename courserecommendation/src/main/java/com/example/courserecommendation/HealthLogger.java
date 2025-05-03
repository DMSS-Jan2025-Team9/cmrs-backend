package com.example.courserecommendation;

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
            logger.info("Health Check [" + name + "]: " + health.getStatus());
            for (Map.Entry<String, Object> entry : health.getDetails().entrySet()) {
                logger.info("  - " + entry.getKey() + ": " + entry.getValue());
            }
        } else if (component instanceof CompositeHealth composite) {
            logger.info("Composite Health [" + name + "]: " + composite.getStatus());
            composite.getComponents().forEach(this::logHealthComponent);
        }
    }

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void logHealthStatus() {
        var health = healthEndpoint.health();
        logger.info("Health status: " + health.getStatus());
        logHealthComponent("root", health);
    }
}