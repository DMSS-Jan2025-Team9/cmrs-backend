package com.example.usermanagement.config;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.data.RepositoryItemWriter;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.StudentRepository;

import java.text.SimpleDateFormat;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StudentItemWriterTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private RepositoryItemWriter<Student> writer;

    @Test
    void testWriter() throws Exception {
        // Create a valid student and set properties using setters
        Student student = new Student();
        student.setName("John Doe");
        student.setProgramId(123L);
        student.setEnrolledAt(new SimpleDateFormat("yyyy-MM-dd").parse("2025-03-01"));

        // Wrap the student in a chunk
        Chunk<Student> chunk = new Chunk<>();
        chunk.add(student);

        // Print the student before writing it
        System.out.println("Writing student: " + student);

        // Set up writer with mock repository and method name
        writer.setRepository(studentRepository);
        writer.setMethodName("save");

        // Perform the write operation
        writer.write(chunk);  // Write the chunk's items

        // Verify that the repository's save method was called once with the correct student
        verify(studentRepository, times(1)).save(student);
    }
}
