package com.example.usermanagement.strategy;

public class RemoveSpecialCharsStrategy implements NameCleaningStrategy {

    @Override
    public String clean(String name) {
        return name.replaceAll("[^a-zA-Z]", "");
    }

}
