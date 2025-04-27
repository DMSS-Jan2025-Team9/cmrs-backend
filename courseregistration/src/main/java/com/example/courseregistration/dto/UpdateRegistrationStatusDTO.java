package com.example.courseregistration.dto;



public class UpdateRegistrationStatusDTO {
    private Long id;
    private String newStatus;
    private int identifier;

    public UpdateRegistrationStatusDTO() {}
    public UpdateRegistrationStatusDTO(Long id, String newStatus, int identifier) {
        this.id = id;
        this.newStatus = newStatus;
        this.identifier = identifier;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public int getIdentifier() { return identifier; }
    public void setIdentifier(int identifier) { this.identifier = identifier; }
}

