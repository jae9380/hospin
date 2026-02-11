package com.hp.hospin.notification.infrastructure;

import com.google.firebase.messaging.*;
import com.hp.hospin.global.metric.ExternalApiMetricHelper;
import com.hp.hospin.notification.domain.port.FCMSender;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMSenderImpl implements FCMSender {
    private static final String NOTIFICATION_TITLE = "HosPin";
    private static final String TTL_HEADER = "300";

    private final ExternalApiMetricHelper metricHelper;
    private final MeterRegistry meterRegistry;

    @Override
    public void send(String token, String postTitle, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", TTL_HEADER)
                        .setNotification(new WebpushNotification(NOTIFICATION_TITLE, body))
                        .build())
                .build();

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            String messageId = FirebaseMessaging.getInstance()
                    .sendAsync(message)
                    .get();

            metricHelper.success("firebase", "fcm.send").increment();

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();

            metricHelper.fail(
                    "firebase",
                    "fcm.send",
                    cause.getClass().getSimpleName()
            ).increment();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            sample.stop(
                    metricHelper.timer("firebase", "fcm.send")
            );
        }
    }
}
