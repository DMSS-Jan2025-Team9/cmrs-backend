package com.example.usermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.usermanagement.strategy.CapitalizeNameStrategy;
import com.example.usermanagement.strategy.CompositeNameCleaningStrategy;
import com.example.usermanagement.strategy.NameCleaningStrategy;
import com.example.usermanagement.strategy.RemoveSpecialCharsStrategy;

@Configuration
public class NameCleaningConfig {

    @Bean
    public NameCleaningStrategy removeSpecialCharsStrategy() {
        return new RemoveSpecialCharsStrategy(); 
    }

    @Bean
    public NameCleaningStrategy capitalizeNameStrategy() {
        return new CapitalizeNameStrategy(); 
    }

    @Bean
    public CompositeNameCleaningStrategy compositeNameCleaningStrategy(
            NameCleaningStrategy removeSpecialCharsStrategy,
            NameCleaningStrategy capitalizeNameStrategy) {
        return new CompositeNameCleaningStrategy(removeSpecialCharsStrategy, capitalizeNameStrategy);
    }
}

