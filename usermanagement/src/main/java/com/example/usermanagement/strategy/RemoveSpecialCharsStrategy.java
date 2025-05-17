package com.example.usermanagement.strategy;

public class RemoveSpecialCharsStrategy implements NameCleaningStrategy {

    @Override
    public String clean(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        return name.replaceAll("[^a-zA-Z]", "");
    }

}
