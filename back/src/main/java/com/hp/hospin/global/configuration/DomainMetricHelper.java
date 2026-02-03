package com.hp.hospin.global.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@RequiredArgsConstructor
@Component
public class DomainMetricHelper {

    private final MeterRegistry meterRegistry;

    public Timer timer(String domain, String layer, String api) {
        return Timer.builder("domain_request_duration_seconds")
                .tag("domain", domain)
                .tag("layer", layer)
                .tag("api", api)
                .register(meterRegistry);
    }

    public Counter success(String domain, String layer, String api) {
        return Counter.builder("domain_requests_total")
                .tag("domain", domain)
                .tag("layer", layer)
                .tag("api", api)
                .tag("result", "success")
                .register(meterRegistry);
    }

    public Counter fail(String domain, String layer, String api) {
        return Counter.builder("domain_requests_total")
                .tag("domain", domain)
                .tag("layer", layer)
                .tag("api", api)
                .tag("result", "fail")
                .register(meterRegistry);
    }
}