package com.example.usermanagement.config;

import com.example.usermanagement.listener.StudentJobCompletionListener;
import com.example.usermanagement.mapper.StudentFieldSetMapper;
import com.example.usermanagement.model.Student;
import com.example.usermanagement.processor.StudentProcessor;
import com.example.usermanagement.repository.StudentRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.repository.UserRoleRepository;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.strategy.CapitalizeNameStrategy;
import com.example.usermanagement.strategy.CompositeNameCleaningStrategy;
import com.example.usermanagement.strategy.NameCleaningStrategy;
import com.example.usermanagement.strategy.RemoveSpecialCharsStrategy;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Profile("test")
public class TestJobConfig {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final StudentJobCompletionListener studentJobCompletionListener;

    public TestJobConfig(StudentRepository studentRepository, StudentJobCompletionListener listener,
            UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.studentJobCompletionListener = listener;
    }

    @Bean
    @StepScope
    @Primary
    public FlatFileItemReader<Student> reader(@Value("#{jobParameters['csvFile']}") String csvFilePath) {
        FlatFileItemReader<Student> csvReader = new FlatFileItemReader<>();

        // Use a test CSV file from classpath instead of relying on jobParameters
        Resource testResource = new ClassPathResource("test-students.csv");
        csvReader.setResource(testResource);

        csvReader.setName("testStudentEnrollmentReader");
        csvReader.setLinesToSkip(1);
        csvReader.setLineMapper(lineMapper());

        return csvReader;
    }

    private LineMapper<Student> lineMapper() {
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("programId", "firstName", "lastName", "enrolledAt");

        StudentFieldSetMapper fieldSetMapper = new StudentFieldSetMapper(userRepository);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    @StepScope
    @Primary
    public StudentProcessor processor(UserService userService, @Value("#{jobParameters['jobId']}") String jobId) {
        // Initialize strategies
        NameCleaningStrategy removeSpecialCharsStrategy = new RemoveSpecialCharsStrategy();
        NameCleaningStrategy capitalizeNameStrategy = new CapitalizeNameStrategy();

        // Create composite strategy
        NameCleaningStrategy compositeStrategy = new CompositeNameCleaningStrategy(removeSpecialCharsStrategy,
                capitalizeNameStrategy);

        // Use a default jobId for tests if none is provided
        String testJobId = (jobId != null) ? jobId : "test-job-id";

        return new StudentProcessor(compositeStrategy, userService, testJobId, userRoleRepository);
    }

    @Bean
    @Primary
    public RepositoryItemWriter<Student> writer() {
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(studentRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    @Primary
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
            UserService userService) {
        return new StepBuilder("test-csv-step", jobRepository)
                .<Student, Student>chunk(10, transactionManager)
                .reader(reader(null))
                .processor(processor(userService, null))
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    @Primary
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
            UserService userService) {
        return new JobBuilder("testImportStudents", jobRepository)
                .listener(studentJobCompletionListener)
                .flow(step1(jobRepository, transactionManager, userService))
                .end().build();
    }

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(5);
        return asyncTaskExecutor;
    }
}