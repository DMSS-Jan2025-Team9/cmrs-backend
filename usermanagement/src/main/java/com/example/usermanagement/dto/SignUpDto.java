package com.example.usermanagement.dto;

import java.util.Set;

import com.example.usermanagement.model.Permission;
import com.example.usermanagement.model.Role;

public class SignUpDto {
    private String username;
    private String email;
    private String password;

    private String role;
    // private Set<String> permissions;

    // Getters and setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // public Set<Permission> getPermissions() {
    //     return permissions;
    // }

    // public void setPermissions(Set<Permission> permissions) {
    //     this.permissions = permissions;
    // }
}
