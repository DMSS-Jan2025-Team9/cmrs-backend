package com.example.usermanagement.strategy;

public class CapitalizeNameStrategy implements NameCleaningStrategy {
    @Override
    public String clean(String name) {
        if (name == null) {
            return "";
        }
        return name.isEmpty()
                ? ""
                : name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
