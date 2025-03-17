package com.example.usermanagement.config;

import com.example.usermanagement.listener.StudentJobCompletionListener;
import com.example.usermanagement.mapper.StudentFieldSetMapper;
import com.example.usermanagement.dto.Student;
import com.example.usermanagement.processor.StudentProcessor;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.strategy.CapitalizeNameStrategy;
import com.example.usermanagement.strategy.CompositeNameCleaningStrategy;
import com.example.usermanagement.strategy.NameCleaningStrategy;
import com.example.usermanagement.strategy.RemoveSpecialCharsStrategy;
//import lombok.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobConfig {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    private final StudentJobCompletionListener studentJobCompletionListener;

    // Explicit Constructor Injection
    public JobConfig(StudentRepository studentRepository, StudentJobCompletionListener listener, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.studentJobCompletionListener = listener;
    }


    // Read information from the source, CSV file
    @Bean
    @StepScope
    public FlatFileItemReader<Student> reader(@Value("#{jobParameters['csvFile']}") String csvFilePath){
        FlatFileItemReader<Student> csvReader = new FlatFileItemReader<>();
        //csvReader.setResource(new FileSystemResource("src/main/resources/student_enrollment_2025.csv"));
        csvReader.setResource(new FileSystemResource(csvFilePath));
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
        lineTokenizer.setNames("programId", "firstName", "lastName","enrolledAt");

//        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
//        fieldSetMapper.setTargetType(Student.class);

        // Use the custom field set mapper with User repository lookup
        StudentFieldSetMapper fieldSetMapper = new StudentFieldSetMapper(userRepository);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    // Processor for student data
//    @Bean
//    public StudentProcessor processor(){
//
//        return new StudentProcessor();
//    }

    @Bean
    public StudentProcessor processor(UserService userService) {
        // Initialize both strategies and pass them to the composite strategy
        NameCleaningStrategy removeSpecialCharsStrategy = new RemoveSpecialCharsStrategy();
        NameCleaningStrategy capitalizeNameStrategy = new CapitalizeNameStrategy();

        // Create the composite strategy
        NameCleaningStrategy compositeStrategy = new CompositeNameCleaningStrategy(removeSpecialCharsStrategy, capitalizeNameStrategy);

        // Pass the composite strategy to the StudentProcessor
        return new StudentProcessor(compositeStrategy, userService);
    }


    // Writer for saving processed data
    @Bean
    public RepositoryItemWriter<Student> writer(){
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(studentRepository);
        writer.setMethodName("save");
        return writer;
    }

    // Step configuration
    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, UserService userService){
        return new StepBuilder("csv-step", jobRepository)
                .<Student, Student>chunk(10, transactionManager)
                .reader(reader(null))
                .processor(processor(userService))
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    // Job configuration
    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, UserService userService){
        return new JobBuilder("importStudents", jobRepository)
                .listener(studentJobCompletionListener) // add listener here
                .flow(step1(jobRepository,transactionManager,userService))
                .end().build();
    }

    // Task Executor to run the steps asynchronously
    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

}
