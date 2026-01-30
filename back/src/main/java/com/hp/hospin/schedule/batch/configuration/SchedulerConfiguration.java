package com.hp.hospin.schedule.batch.configuration;

import com.hp.hospin.hospital.batch.listener.JobListener;
import com.hp.hospin.schedule.batch.itemProcessor.ScheduleProcessor;
import com.hp.hospin.schedule.batch.itemReader.ScheduleReader;
import com.hp.hospin.schedule.batch.itemWriter.ScheduleWriter;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.infrastructure.entity.JpaScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SchedulerConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager ptm;
    private final JobListener jobListener;

    //    Reader
    private final ScheduleReader reader;

    //    Processor
    private final ScheduleProcessor processor;

    //    Writer
    private final ScheduleWriter writer;

    @Bean(name = "scheduleJob")
    public Job scheduleJob() {
        return new JobBuilder("scheduleJob", jobRepository)
                .start(scheduleStep())
                .listener(jobListener)
                .build();
    }

    @JobScope
    @Bean
    public Step scheduleStep() {
        return new StepBuilder("scheduleStep", jobRepository)
                .<JpaScheduleEntity, Schedule>chunk(1000, ptm)
                .reader(reader.scheduleItemReader())
                .processor(processor)
                .writer(writer)
                .build();
    }
}
