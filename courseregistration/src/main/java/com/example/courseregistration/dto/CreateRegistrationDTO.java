package com.example.courseregistration.dto;
import java.util.List;

public class CreateRegistrationDTO {
    private List<String> studentFullIds; 
    private Long classId;

    public List<String> getStudentFullIds() {
        return studentFullIds;
    }

    public void setStudentFullIds(List<String> studentFullIds) {
        this.studentFullIds = studentFullIds;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }
}