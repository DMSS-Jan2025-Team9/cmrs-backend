package com.example.courseregistration.dto;
import java.util.List;

public class CreateRegistrationDTO {
    private List<Long> studentIds; 
    private Long classId;

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }
}