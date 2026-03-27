package com.hp.hospin.batch.listener;

import com.hp.hospin.global.metric.BatchMetricHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobListener implements JobExecutionListener {

    private final BatchMetricHelper metricHelper;

    private long startTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = System.currentTimeMillis();
        log.info("🔥 JobListener beforeJob called: {}",
                jobExecution.getJobInstance().getJobName());
        metricHelper.jobStart(
                jobExecution.getJobInstance().getJobName()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        jobExecution.getExecutionContext().putLong("executionTime", executionTime);

        String jobName = jobExecution.getJobInstance().getJobName();
        BatchStatus status = jobExecution.getStatus();

        metricHelper.recordJobExecutionTime(
                jobName,
                executionTime,
                status
        );

        if (status == BatchStatus.COMPLETED) {
            metricHelper.jobSuccess(jobName);
        } else {
            metricHelper.jobFail(jobName, status.name());
        }
    }
}