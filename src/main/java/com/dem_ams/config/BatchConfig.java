package com.dem_ams.config;

import com.dem_ams.batch.JobCompletionNotificationListener;
import com.dem_ams.batch.LeaseStatusProcessor;
import com.dem_ams.domain.model.Lease;
import com.dem_ams.repository.LeaseRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

//    private final LeaseRepository leaseRepository;
//
//    public BatchConfig(LeaseRepository leaseRepository) {
//        this.leaseRepository = leaseRepository;
//    }
//
//    @Bean
//    public LeaseStatusProcessor leaseStatusProcessor() {
//        return new LeaseStatusProcessor(leaseRepository);
//    }
//
//    @Bean
//    public JdbcBatchItemWriter<Lease> writer(DataSource dataSource) {
//        return new JdbcBatchItemWriterBuilder<Lease>()
//                .dataSource(dataSource)
//                .sql("UPDATE leases SET status = :EXPIRED WHERE due_date <= :CURRENT_DATE")
//                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                .beanMapped()
//                .build();
//    }
//
//    @Bean
//    public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
//        return new JobBuilder(jobRepository)
//                .listener(listener)
//                .start(step1)
//                .build();
//    }
//
//    @Bean
//    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
//                    LeaseStatusProcessor processor, JdbcBatchItemWriter<Lease> writer) {
//        return new StepBuilder(jobRepository)
//                .<Lease, Lease>chunk(3)
//                .transactionManager(transactionManager)
//                .processor(processor)
//                .writer(writer)
//                .build();
//    }
}
