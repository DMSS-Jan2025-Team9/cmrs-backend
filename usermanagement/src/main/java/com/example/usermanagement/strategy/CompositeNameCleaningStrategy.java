package com.example.usermanagement.strategy;

public class CompositeNameCleaningStrategy implements NameCleaningStrategy {
    private final NameCleaningStrategy removeSpecialCharsStrategy;
    private final NameCleaningStrategy capitalizeNameStrategy;

    // Constructor to inject both strategies
    public CompositeNameCleaningStrategy(NameCleaningStrategy removeSpecialCharsStrategy, NameCleaningStrategy capitalizeNameStrategy) {
        this.removeSpecialCharsStrategy = removeSpecialCharsStrategy;
        this.capitalizeNameStrategy = capitalizeNameStrategy;
    }

    @Override
    public String clean(String name) {
        // First, remove special characters, then capitalize the name
        String cleanedName = removeSpecialCharsStrategy.clean(name);
        return capitalizeNameStrategy.clean(cleanedName);
    }
}
