package com.hp.hospin.global.metric;

import com.hp.hospin.global.standard.annotations.Monitored;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
@Aspect
@Component
@RequiredArgsConstructor
public class DomainMetricAspectConfig {
    private final MeterRegistry meterRegistry;
    private final DomainMetricHelper metricHelper;

    @Around("@annotation(monitored)")
    public Object measure(
            ProceedingJoinPoint joinPoint,
            Monitored monitored
    ) throws Throwable {

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Object result = joinPoint.proceed();

            metricHelper.success(
                    monitored.domain(),
                    monitored.layer(),
                    monitored.api()
            ).increment();

            return result;

        } catch (Exception e) {

            metricHelper.fail(
                    monitored.domain(),
                    monitored.layer(),
                    monitored.api()
            ).increment();

            throw e;

        } finally {
            sample.stop(
                    metricHelper.timer(
                            monitored.domain(),
                            monitored.layer(),
                            monitored.api()
                    )
            );
        }
    }
}
