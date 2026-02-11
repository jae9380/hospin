package com.hp.hospin.global.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class BatchMetricHelper {

    private final MeterRegistry meterRegistry;

    /* ======================
       Job Lifecycle
       ====================== */

    /**
     * Job 시작 횟수
     */
    public void jobStart(String jobName) {
        jobStartCounter(jobName).increment();
    }

    /**
     * Job 성공
     */
    public void jobSuccess(String jobName) {
        jobSuccessCounter(jobName).increment();
    }

    /**
     * Job 실패
     */
    public void jobFail(String jobName, String reason) {
        jobFailCounter(jobName, reason).increment();
    }

    /**
     * Job 실행 시간 기록 (ms)
     */
    public void recordJobExecutionTime(
            String jobName,
            long executionTimeMs,
            BatchStatus status
    ) {
        jobExecutionTimer(jobName, status.name())
                .record(executionTimeMs, TimeUnit.MILLISECONDS);
    }

    /* ======================
       Metric Definition
       ====================== */

    private Counter jobStartCounter(String jobName) {
        return Counter.builder("batch.job.start.total")
                .tag("job", jobName)
                .register(meterRegistry);
    }

    private Counter jobSuccessCounter(String jobName) {
        return Counter.builder("batch.job.success.total")
                .tag("job", jobName)
                .register(meterRegistry);
    }

    private Counter jobFailCounter(String jobName, String reason) {
        return Counter.builder("batch.job.fail.total")
                .tag("job", jobName)
                .tag("reason", reason)
                .register(meterRegistry);
    }

    private Timer jobExecutionTimer(String jobName, String status) {
        return Timer.builder("batch.job.execution.time")
                .tag("job", jobName)
                .tag("status", status)
                .publishPercentileHistogram(true) // p95 가능
                .register(meterRegistry);
    }
}