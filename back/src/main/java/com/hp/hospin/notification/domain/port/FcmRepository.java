package com.hp.hospin.notification.domain.port;

import com.hp.hospin.notification.domain.entity.FCM;

import java.util.Optional;

public interface FcmRepository {
    void save(FCM fcm);
    Optional<FCM> findFCMByUserId(Long userId);
}
