package com.hp.hospin.global.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalApiMetricHelper {
    private final MeterRegistry meterRegistry;

    public Counter success(String system, String operation) {
        return Counter.builder("external_api_requests_total")
                .tag("system", system)
                .tag("operation", operation)
                .tag("result", "success")
                .register(meterRegistry);
    }

    public Counter fail(String system, String operation, String errorType) {
        return Counter.builder("external_api_requests_total")
                .tag("system", system)
                .tag("operation", operation)
                .tag("result", "fail")
                .tag("error_type", errorType)
                .register(meterRegistry);
    }

    public Timer timer(String system, String operation) {
        return Timer.builder("external_api_request_duration")
                .tag("system", system)
                .tag("operation", operation)
                .publishPercentileHistogram(true)   // ⭐ 필수
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }
}