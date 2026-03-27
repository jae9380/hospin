package com.hp.hospin.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobScheduler {
    private final JobLauncher jobLauncher;
    private final Job scheduleJob;

    @Scheduled(cron = "0 */1 * * * *")
    public void runScheduleJob() throws Exception {
        String batchId = "scheduleJob-" +
                LocalDateTime.now()
                        .truncatedTo(ChronoUnit.MINUTES)
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("batchId", batchId)
                .toJobParameters();

        jobLauncher.run(scheduleJob, jobParameters);
    }
}
