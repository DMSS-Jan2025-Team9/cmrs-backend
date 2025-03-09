package com.example.usermanagement.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Student {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long studentId;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
    @SequenceGenerator(name = "student_seq", sequenceName = "student_sequence", allocationSize = 1)
    private Long studentId;  // This is the sequence-generated number

    private String studentFullId;  // The full student ID with prefix

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private Long programId;

    private Date enrolledAt;

    private String firstName;

    private String lastName;

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Date getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Date enrolledAt) {
        this.enrolledAt = enrolledAt;
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

    public String getStudentFullId() {
        return studentFullId;
    }

    public void setStudentFullId(String studentFullId) {
        this.studentFullId = studentFullId;
    }

    @Override
    public String toString() {
        return "Student{" +
                "programId='" + programId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", name='" + name + '\'' +
                ", studentId='" + studentFullId + '\'' +
                ", enrolledAt='" + enrolledAt + '\'' +
                '}';
    }
}
