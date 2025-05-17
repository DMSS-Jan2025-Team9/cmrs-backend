package com.example.usermanagement.config;

import com.example.usermanagement.strategy.CapitalizeNameStrategy;
import com.example.usermanagement.strategy.CompositeNameCleaningStrategy;
import com.example.usermanagement.strategy.NameCleaningStrategy;
import com.example.usermanagement.strategy.RemoveSpecialCharsStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NameCleaningConfigTest {

    @Test
    public void testRemoveSpecialCharsStrategy() {
        // Arrange
        NameCleaningConfig config = new NameCleaningConfig();

        // Act
        NameCleaningStrategy strategy = config.removeSpecialCharsStrategy();

        // Assert
        assertNotNull(strategy);
        assertTrue(strategy instanceof RemoveSpecialCharsStrategy);

        // Test functionality
        assertEquals("JohnDoe", strategy.clean("John Doe"));
    }

    @Test
    public void testCapitalizeNameStrategy() {
        // Arrange
        NameCleaningConfig config = new NameCleaningConfig();

        // Act
        NameCleaningStrategy strategy = config.capitalizeNameStrategy();

        // Assert
        assertNotNull(strategy);
        assertTrue(strategy instanceof CapitalizeNameStrategy);

        // Test functionality
        assertEquals("John", strategy.clean("john"));
    }

    @Test
    public void testCompositeNameCleaningStrategy() {
        // Arrange
        NameCleaningConfig config = new NameCleaningConfig();
        NameCleaningStrategy removeStrategy = config.removeSpecialCharsStrategy();
        NameCleaningStrategy capitalizeStrategy = config.capitalizeNameStrategy();

        // Act
        CompositeNameCleaningStrategy compositeStrategy = config.compositeNameCleaningStrategy(
                removeStrategy, capitalizeStrategy);

        // Assert
        assertNotNull(compositeStrategy);

        // Test functionality
        assertEquals("John", compositeStrategy.clean("john"));
        assertEquals("Johndoe", compositeStrategy.clean("john doe"));
    }
}