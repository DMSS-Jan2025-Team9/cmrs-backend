package com.example.usermanagement.model;

import jakarta.persistence.*;

@Entity
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer staffId;

    private String name;

    private String firstName;

    private String lastName;

    private String staffFullId;

    private String department;

    private String position;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters
    public Integer getStaffId() {
        return staffId;
    }

    public void setAdminId(Integer staffId) {
        this.staffId = staffId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStaffFullId() {
        return staffFullId;
    }

    public void setStaffFullId(String staffFullId) {
        this.staffFullId = staffFullId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}
