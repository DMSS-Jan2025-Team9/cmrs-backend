package com.example.usermanagement.mapper;

import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StudentFieldSetMapper implements FieldSetMapper<Student> {

    private final UserRepository userRepository;

    @Autowired
    public StudentFieldSetMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Student mapFieldSet(FieldSet fieldSet) {
        // Convert fields from the CSV to Student object
        Student student = new Student();
        student.setProgramId(fieldSet.readLong("programId"));
        student.setFirstName(fieldSet.readString("firstName"));
        student.setLastName(fieldSet.readString("lastName"));

        // Parse the date
        String dateString = fieldSet.readString("enrolledAt");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  // Adjust format if needed
        try {
            Date enrolledAt = dateFormat.parse(dateString);
            student.setEnrolledAt(enrolledAt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return student;
    }
}
