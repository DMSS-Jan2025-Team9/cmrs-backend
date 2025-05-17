package com.example.usermanagement.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NameCleaningStrategyTest {

    @Mock
    private NameCleaningStrategy removeSpecialCharsStrategy;

    @Mock
    private NameCleaningStrategy capitalizeNameStrategy;

    private CompositeNameCleaningStrategy compositeStrategy;
    private CapitalizeNameStrategy realCapitalizeStrategy;
    private RemoveSpecialCharsStrategy realRemoveSpecialCharsStrategy;

    @BeforeEach
    public void setup() {
        compositeStrategy = new CompositeNameCleaningStrategy(removeSpecialCharsStrategy, capitalizeNameStrategy);
        realCapitalizeStrategy = new CapitalizeNameStrategy();
        realRemoveSpecialCharsStrategy = new RemoveSpecialCharsStrategy();
    }

    @Test
    public void testCompositeNameCleaningStrategy() {
        // Arrange
        String input = "test-name";
        String intermediateResult = "testname";
        String expectedResult = "Testname";

        when(removeSpecialCharsStrategy.clean(input)).thenReturn(intermediateResult);
        when(capitalizeNameStrategy.clean(intermediateResult)).thenReturn(expectedResult);

        // Act
        String result = compositeStrategy.clean(input);

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    public void testCapitalizeNameStrategy() {
        // Test with lowercase name
        assertEquals("John", realCapitalizeStrategy.clean("john"));

        // Test with uppercase name
        assertEquals("John", realCapitalizeStrategy.clean("JOHN"));

        // Test with mixed case name
        assertEquals("John", realCapitalizeStrategy.clean("jOhN"));

        // Test with empty string
        assertEquals("", realCapitalizeStrategy.clean(""));

        // Test with null
        assertEquals("", realCapitalizeStrategy.clean(null));
    }

    @Test
    public void testRemoveSpecialCharsStrategy() {
        // Test with special characters
        assertEquals("JohnDoe", realRemoveSpecialCharsStrategy.clean("John-Doe"));
        assertEquals("JohnDoe", realRemoveSpecialCharsStrategy.clean("John@Doe"));
        assertEquals("JohnDoe", realRemoveSpecialCharsStrategy.clean("John Doe"));

        // Test with no special characters
        assertEquals("JohnDoe", realRemoveSpecialCharsStrategy.clean("JohnDoe"));

        // Test with empty string
        assertEquals("", realRemoveSpecialCharsStrategy.clean(""));

        // Test with null
        assertEquals("", realRemoveSpecialCharsStrategy.clean(null));
    }
}