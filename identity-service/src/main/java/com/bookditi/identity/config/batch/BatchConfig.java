package com.bookditi.identity.config.batch;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.bookditi.identity.dto.UserDto;
import com.bookditi.identity.entity.User;
import com.bookditi.identity.listener.JobCompletionNotificationListener;
import com.bookditi.identity.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BatchConfig {
    @Value("${spring.batch.file-path.output}")
    private String outputFile;

    private final UserRepository userRepository;

    public BatchConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public RepositoryItemReader<User> itemReader() {
        final Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);

        return new RepositoryItemReaderBuilder<User>()
                .name("userItemReader")
                .repository(userRepository)
                .methodName("findAll")
                .pageSize(10)
                .arguments(Collections.emptyList()) // Không có tham số
                .sorts(sorts)
                .build();
    }

    @Bean
    public UserItemProcessor userProcessor() {
        return new UserItemProcessor();
    }

    @Bean
    public FlatFileItemWriter<UserDto> itemWriter() {
        BeanWrapperFieldExtractor<UserDto> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"id", "username", "firstName", "lastName", "dob", "roles"});

        DelimitedLineAggregator<UserDto> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(", ");
        aggregator.setFieldExtractor(extractor);

        return new FlatFileItemWriterBuilder<UserDto>()
                .name("userItemWriter")
                .resource(new FileSystemResource(outputFile))
                .headerCallback(writer -> writer.write("id, username, firstName, lastName, dob, roles"))
                .lineAggregator(aggregator)
                .build();
    }

    @Bean
    public Step exportUser(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            RepositoryItemReader<User> reader,
            UserItemProcessor processor,
            FlatFileItemWriter<UserDto> writer) {

        return new StepBuilder("step1", jobRepository)
                .<User, UserDto>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public JobCompletionNotificationListener executionListener() {
        return new JobCompletionNotificationListener();
    }

    @Bean
    public Job job(JobRepository jobRepository, JobCompletionNotificationListener listener, Step exportUser) {
        return new JobBuilder("exportUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(exportUser)
                .end()
                .build();
    }
}
