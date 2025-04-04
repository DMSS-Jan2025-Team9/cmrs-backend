// package com.example.usermanagement.config;

// import com.example.usermanagement.listener.StudentJobCompletionListener;
// import com.example.usermanagement.model.Student;
// import com.example.usermanagement.repository.StudentRepository;
// import com.example.usermanagement.repository.UserRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.batch.item.ExecutionContext;
// import org.springframework.batch.item.file.FlatFileItemReader;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// class StudentItemReaderTest {

//     @Mock
//     private StudentRepository studentRepository;

//     @Mock
//     private UserRepository userRepository;

//     @Mock
//     private StudentJobCompletionListener studentJobCompletionListener;

//     @InjectMocks
//     private JobConfig jobConfig;

//     private FlatFileItemReader<Student> reader;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//         reader = jobConfig.reader("src/main/resources/student_enrollment_2025.csv");
//     }

//     @Test
//     void testCsvReader() throws Exception {
//         ExecutionContext executionContext = new ExecutionContext();
//         reader.open(executionContext);

//         Student student;  // Declare once
//         while ((student = reader.read()) != null) {  // Read all students
//             System.out.println("Read student: " + student);
//             assertNotNull(student, "Student should not be null");  // Check each student
//         }

//         reader.close();
//     }

// }
