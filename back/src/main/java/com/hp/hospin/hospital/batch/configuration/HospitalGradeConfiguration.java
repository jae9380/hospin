package com.hp.hospin.hospital.batch.configuration;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;
import com.hp.hospin.hospital.batch.dto.HospitalCodeWithDepartments;
import com.hp.hospin.hospital.batch.dto.HospitalDetailRegister;
import com.hp.hospin.hospital.batch.dto.HospitalGradeRegister;
import com.hp.hospin.hospital.batch.dto.HospitalRegister;
import com.hp.hospin.hospital.batch.itemProcessor.HospitalDepartmentProcessor;
import com.hp.hospin.hospital.batch.itemProcessor.HospitalDetailProcessor;
import com.hp.hospin.hospital.batch.itemProcessor.HospitalGradeProcessor;
import com.hp.hospin.hospital.batch.itemProcessor.HospitalProcessor;
import com.hp.hospin.hospital.batch.itemReader.GroupedHospitalDepartmentReader;
import com.hp.hospin.hospital.batch.itemReader.HospitalDetailReader;
import com.hp.hospin.hospital.batch.itemReader.HospitalGradeReader;
import com.hp.hospin.hospital.batch.itemReader.HospitalReader;
import com.hp.hospin.hospital.batch.itemWriter.HospitalDepartmentWriter;
import com.hp.hospin.hospital.batch.itemWriter.HospitalDetailWriter;
import com.hp.hospin.hospital.batch.itemWriter.HospitalGradeWriter;
import com.hp.hospin.hospital.batch.itemWriter.HospitalWriter;
import com.hp.hospin.hospital.batch.listener.JobListener;
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
public class HospitalGradeConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager ptm;
    private final JobListener jobListener;

//    Reader
    private final HospitalGradeReader hospitalGradeReader;
    private final HospitalReader hospitalReader;
    private final HospitalDetailReader hospitalDetailReader;
    private final GroupedHospitalDepartmentReader groupedHospitalDepartmentReader;

//    Processor
    private final HospitalGradeProcessor hospitalGradeProcessor;
    private final HospitalProcessor hospitalProcessor;
    private final HospitalDetailProcessor hospitalDetailProcessor;
    private final HospitalDepartmentProcessor hospitalDepartmentProcessor;

//    Writer
    private final HospitalGradeWriter hospitalGradeWriter;
    private final HospitalWriter hospitalWriter;
    private final HospitalDetailWriter hospitalDetailWriter;
    private final HospitalDepartmentWriter hospitalDepartmentWriter;

    @Bean(name ="loadHospitalJob" )
    public Job loadHospitalJob() {
        return new JobBuilder("loadHospitalJob", jobRepository)
                .start(loadHospitalStep())
                .listener(jobListener)
                .build();

    }

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

    @Bean(name ="loadHospitalGradeJob" )
    public Job loadHospitalGradeJob() {
        return new JobBuilder("loadHospitalGradeJob", jobRepository)
                .start(loadHospitalGradeStep())
                .listener(jobListener)
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

    @Bean(name ="loadHospitalDetailJob" )
    public Job loadHospitalDetailJob() {
        return new JobBuilder("loadHospitalDetailJob", jobRepository)
//                .start(loadHospitalDetailStep())
                .start(loadHospitalDepartmentStep())
//                .next(loadHospitalDepartmentStep())
                .listener(jobListener)
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

}