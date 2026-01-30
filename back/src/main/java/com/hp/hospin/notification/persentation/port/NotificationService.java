package com.hp.hospin.notification.persentation.port;

public interface NotificationService {
    void register(Long memberId, String token);
    void deregister(Long memberId);
    void push(Long userId, String title, long diffHours, long diffMinutes);
}
