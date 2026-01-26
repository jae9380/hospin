package com.hp.hospin.notification.application.port;

public interface FcmDomainService {
    String register(Long memberId, String token);
    void deregister(Long memberId);
    void sendFCM(String token, String message);
    String getMessage(String title, long diffHours, long diffMinutes);
}
