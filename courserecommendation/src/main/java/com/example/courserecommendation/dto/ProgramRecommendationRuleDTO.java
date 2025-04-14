package com.example.courserecommendation.dto;

public class ProgramRecommendationRuleDTO {
    private Long ruleId;

    private Long programId; // Foreign key to the program this rule belongs to


    // Type of rule - RecommendationConstants.RuleMatchType: e.g., TITLE_CONTAINS, CATEGORY_EQUALS, LEVEL_EQUALS
    private String type;

    // The value to match against (e.g., "Java", "Data Science", "Advanced")
    private String value;

    // The score to apply if this rule matches
    private double weight;

    public ProgramRecommendationRuleDTO() {}

    public ProgramRecommendationRuleDTO(Long ruleId, Long programId, String type, String value, double weight) {
        this.ruleId = ruleId;
        this.programId = programId;
        this.type = type;
        this.value = value;
        this.weight = weight;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public Long getProgramId() {
        return programId;
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