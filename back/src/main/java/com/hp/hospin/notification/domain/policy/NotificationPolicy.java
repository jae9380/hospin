package com.hp.hospin.notification.domain.policy;

import java.time.Duration;
import java.time.LocalDateTime;

public enum NotificationPolicy {
    BEFORE_24H(Duration.ofHours(24), "하루 전 알림"),
    BEFORE_6H (Duration.ofHours(6),  "6시간 전 알림"),
    BEFORE_3H (Duration.ofHours(3),  "3시간 전 알림"),
    BEFORE_1H (Duration.ofHours(1),  "1시간 전 알림");

    private final Duration before;
    private final String description;

    NotificationPolicy(Duration before, String description) {
        this.before = before;
        this.description = description;
    }

    public LocalDateTime notifyAt(LocalDateTime scheduleAt) {
        return scheduleAt.minus(before);
    }
}
