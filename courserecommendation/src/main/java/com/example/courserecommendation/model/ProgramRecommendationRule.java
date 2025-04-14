package com.example.courserecommendation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "program_recommendation_rules")
public class ProgramRecommendationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    private Long programId; // Foreign key to the program this rule belongs to

    private String type;    // e.g., "TITLE_CONTAINS", "CATEGORY_EQUALS", "LEVEL_EQUALS"
    private String value;   // e.g., "Java", "Data Science"
    private double weight;  // Score weight if matched

    public ProgramRecommendationRule() {}

    public ProgramRecommendationRule(Long programId, String type, String value, double weight) {
        this.programId = programId;
        this.type = type;
        this.value = value;
        this.weight = weight;
    }

    // Getters and setters...

    public Long getRuleId() {
        return ruleId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
