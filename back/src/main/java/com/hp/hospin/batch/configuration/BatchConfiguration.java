package com.hp.hospin.batch.configuration;

import com.hp.hospin.batch.dto.HospitalCodeWithDepartments;
import com.hp.hospin.batch.dto.HospitalDetailRegister;
import com.hp.hospin.batch.dto.HospitalGradeRegister;
import com.hp.hospin.batch.dto.HospitalRegister;
import com.hp.hospin.batch.itemProcessor.*;
import com.hp.hospin.batch.itemReader.*;
import com.hp.hospin.batch.itemWriter.*;
import com.hp.hospin.batch.listener.JobListener;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;
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
public class BatchConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager ptm;
    private final JobListener jobListener;

//    Reader
// ====== Hospital ======
    private final HospitalGradeReader hospitalGradeReader;
    private final HospitalReader hospitalReader;
    private final HospitalDetailReader hospitalDetailReader;
    private final GroupedHospitalDepartmentReader groupedHospitalDepartmentReader;

// ====== Schedule ======
    private final ScheduleReader reader;

//    Processor
// ====== Hospital ======
    private final HospitalGradeProcessor hospitalGradeProcessor;
    private final HospitalProcessor hospitalProcessor;
    private final HospitalDetailProcessor hospitalDetailProcessor;
    private final HospitalDepartmentProcessor hospitalDepartmentProcessor;

// ====== Schedule ======
    private final ScheduleProcessor processor;

//    Writer
// ====== Hospital ======
    private final HospitalGradeWriter hospitalGradeWriter;
    private final HospitalWriter hospitalWriter;
    private final HospitalDetailWriter hospitalDetailWriter;
    private final HospitalDepartmentWriter hospitalDepartmentWriter;

// ====== Schedule ======
    private final ScheduleWriter writer;


//    JOB
// ====== Hospital ======
    @Bean(name ="loadHospitalJob" )
    public Job loadHospitalJob() {
        return new JobBuilder("loadHospitalJob", jobRepository)
                .start(loadHospitalStep())
                .listener(jobListener)
                .build();

    }

    @Bean(name ="loadHospitalGradeJob" )
    public Job loadHospitalGradeJob() {
        return new JobBuilder("loadHospitalGradeJob", jobRepository)
                .start(loadHospitalGradeStep())
                .listener(jobListener)
                .build();

    }
    @Bean(name ="loadHospitalDetailJob" )
    public Job loadHospitalDetailJob() {
        return new JobBuilder("loadHospitalDetailJob", jobRepository)
                .start(loadHospitalDetailStep())
//                .start(loadHospitalDepartmentStep())
                .next(loadHospitalDepartmentStep())
                .listener(jobListener)
                .build();

    }

// ====== Schedule ======
    @Bean(name = "scheduleJob")
    public Job scheduleJob() {
        return new JobBuilder("scheduleJob", jobRepository)
                .start(scheduleStep())
                .listener(jobListener)
                .build();
    }


//    STEP
// ====== Hospital ======
    @JobScope
    @Bean
    public Step loadHospitalStep() {
        return new StepBuilder("loadHospitalStep", jobRepository)
                .<HospitalRegister, JpaHospitalEntity>chunk(1000,ptm)
                .reader(hospitalReader.readerByCSV())
                .processor(hospitalProcessor)
                .writer(hospitalWriter)
                .build();
    }

    @JobScope
    @Bean
    public Step loadHospitalGradeStep() {
        return new StepBuilder("loadHospitalGradeStep", jobRepository)
                .<HospitalGradeRegister, JpaHospitalGradeEntity>chunk(1000,ptm)
                .reader(hospitalGradeReader.read())
                .processor(hospitalGradeProcessor)
                .writer(hospitalGradeWriter)
                .build();
    }

    @JobScope
    @Bean
    public Step loadHospitalDetailStep() {
        return new StepBuilder("loadHospitalDetailStep", jobRepository)
                .<HospitalDetailRegister, JpaHospitalDetailEntity>chunk(1000,ptm)
                .reader(hospitalDetailReader.readerByHospitalDetail())
                .processor(hospitalDetailProcessor)
                .writer(hospitalDetailWriter)
                .build();
    }

    @JobScope
    @Bean
    public Step loadHospitalDepartmentStep() {
        return new StepBuilder("loadHospitalDepartmentStep", jobRepository)
                .<HospitalCodeWithDepartments, JpaHospitalDetailEntity>chunk(1000,ptm)
                .reader(groupedHospitalDepartmentReader)
                .processor(hospitalDepartmentProcessor)
                .writer(hospitalDepartmentWriter)
                .build();
    }

// ====== Schedule ======
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