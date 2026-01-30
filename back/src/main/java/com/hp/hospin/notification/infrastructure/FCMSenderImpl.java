package com.hp.hospin.notification.infrastructure;

import com.google.firebase.messaging.*;
import com.hp.hospin.notification.domain.port.FCMSender;
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

    @Override
    public void send(String token, String postTitle, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", TTL_HEADER)
                        .setNotification(new WebpushNotification(NOTIFICATION_TITLE, body))
                        .build())
                .build();

        try {
            String messageId = FirebaseMessaging.getInstance()
                    .sendAsync(message)
                    .get();

            log.info("[FCM SUCCESS] messageId={}", messageId);

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();

            if (cause instanceof FirebaseMessagingException fme) {
                log.error("[FCM FAIL] code={}, message={}",
                        fme.getErrorCode(), fme.getMessage());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
