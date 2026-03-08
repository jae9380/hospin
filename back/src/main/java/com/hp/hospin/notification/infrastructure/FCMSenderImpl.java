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

        log.info("[FCM TRY SEND] token={}, body={}", token, body);
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

            log.info(
                    "[FCM SEND SUCCESS] messageId={}, token={}, title={}, body={}",
                    messageId,
                    token,
                    postTitle,
                    body
            );
            metricHelper.success("firebase", "fcm.send").increment();

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();

            metricHelper.fail(
                    "firebase",
                    "fcm.send",
                    cause.getClass().getSimpleName()
            ).increment();
            log.error("[FCM SEND ERROR] ExecutionException: {}", cause.getMessage(), cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[FCM SEND ERROR] InterruptedException: {}", e.getMessage(), e);
        } finally {
            sample.stop(
                    metricHelper.timer("firebase", "fcm.send")
            );
        }
    }
}
