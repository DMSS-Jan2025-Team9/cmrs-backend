package com.example.usermanagement.strategy;

public class CapitalizeNameStrategy implements NameCleaningStrategy {
    @Override
    public String clean(String name) {
        return name != null && !name.isEmpty()
                ? name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase()
                : name;
    }
}
