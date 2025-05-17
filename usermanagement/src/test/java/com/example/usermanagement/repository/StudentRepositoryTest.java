package com.example.usermanagement.repository;

import com.example.usermanagement.model.Student;
import com.example.usermanagement.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    private Student testStudent;
    private User testUser;
    private String programName = "Computer Science";
    private String jobId = "job" + System.currentTimeMillis();

    @BeforeEach
    public void setup() {
        // Create test user
        testUser = new User();
        testUser.setUsername("studentuser" + System.currentTimeMillis());
        testUser.setEmail("student" + System.currentTimeMillis() + "@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        // Create test student
        testStudent = new Student();
        testStudent.setStudentFullId("U" + System.currentTimeMillis());
        testStudent.setName("Test Student");
        testStudent.setEnrolledAt(new Date());
        testStudent.setProgramName(programName);
        testStudent.setJobId(jobId);
        testStudent.setUser(testUser);
        testStudent = studentRepository.save(testStudent);

        // Create additional students with the same program and job for testing
        Student student2 = new Student();
        student2.setStudentFullId("U" + (System.currentTimeMillis() + 1));
        student2.setName("Test Student 2");
        student2.setEnrolledAt(new Date());
        student2.setProgramName(programName);
        student2.setJobId(jobId);
        studentRepository.save(student2);

        // Create a student with different program
        Student student3 = new Student();
        student3.setStudentFullId("U" + (System.currentTimeMillis() + 2));
        student3.setName("Test Student 3");
        student3.setEnrolledAt(new Date());
        student3.setProgramName("Mathematics");
        student3.setJobId("different-job");
        studentRepository.save(student3);
    }

    @Test
    public void testFindByStudentFullId() {
        // Act
        Optional<Student> found = studentRepository.findBystudentFullId(testStudent.getStudentFullId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(testStudent.getStudentFullId(), found.get().getStudentFullId());
    }

    @Test
    public void testFindByProgramName() {
        // Act
        List<Student> found = studentRepository.findByProgramName(programName);

        // Assert
        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(s -> s.getProgramName().equals(programName)));
    }

    @Test
    public void testFindByJobId() {
        // Act
        List<Student> found = studentRepository.findByJobId(jobId);

        // Assert
        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(s -> s.getJobId().equals(jobId)));
    }

    @Test
    public void testFindByUserId() {
        // Act
        Optional<Student> found = studentRepository.findByUser_UserId(testUser.getUserId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(testUser.getUserId(), found.get().getUser().getUserId());
        assertEquals(testStudent.getStudentFullId(), found.get().getStudentFullId());
    }
}