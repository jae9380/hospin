package com.hp.hospin.hospital.infrastructure.infrastructure;

import com.hp.hospin.global.metric.ExternalApiMetricHelper;
import com.hp.hospin.hospital.application.port.SymptomAnalyzePort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiSymptomAnalyzeAdapter implements SymptomAnalyzePort {
    private final ChatClient symptomAnalyzeChatClient;
    private final ExternalApiMetricHelper metricHelper;
    private final MeterRegistry meterRegistry;

    @Override
    public String analyze(String text) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            String result = symptomAnalyzeChatClient.prompt()
                    .user(text)
                    .call()
                    .content();

            metricHelper.success("openai", "symptom.analyze").increment();
            return result;
        } catch (Exception e) {

            metricHelper.fail(
                    "openai",
                    "symptom.analyze",
                    e.getClass().getSimpleName()
            ).increment();

            throw e;

        } finally {
            sample.stop(
                    metricHelper.timer("openai", "symptom.analyze")
            );
        }
    }
}
