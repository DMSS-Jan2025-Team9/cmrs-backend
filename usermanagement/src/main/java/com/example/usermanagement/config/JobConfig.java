package com.example.usermanagement.config;

import com.example.usermanagement.model.Student;
import com.example.usermanagement.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class JobConfig {

    private JobBuilder jobBuilder;

    private StepBuilder stepBuilder;

    private StudentRepository studentRepository;

    // Read information from the source, CSV file
    @Bean
    public FlatFileItemReader<Student> reader(){
        FlatFileItemReader<Student> csvReader = new FlatFileItemReader<>();
        csvReader.setResource(new FileSystemResource("src/main/resources/student_enrollment_2025.csv"));
        csvReader.setName("studentEnrollmentReader");
        csvReader.setLinesToSkip(1);
        csvReader.setLineMapper(lineMapper());

        return csvReader;
    }

    // how to read the csv file
    // line mapper for csv parsing
    private LineMapper<Student> lineMapper(){
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("First Name","Last Name","Program Id","Program Name","Enrolled At");

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    // Processor for student data
    @Bean
    public StudentProcessor processor(){
        return new StudentProcessor();
    }

    // Writer for saving processed data
    @Bean
    public RepositoryItemWriter<Student> writer(){
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(studentRepository);
        writer.setMethodName("save");
        return writer;
    }

    

}
