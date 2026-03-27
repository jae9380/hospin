package com.hp.hospin.notification.domain.port;

public interface FCMSender {
    void send(String token, String postTitle, String body);
}
