package com.hp.hospin.notification.application;

import com.hp.hospin.global.standard.util.FcmTokenUtil;
import com.hp.hospin.notification.application.port.FcmDomainService;
import com.hp.hospin.notification.domain.port.FcmRedisTemplate;
import com.hp.hospin.notification.persentation.port.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmServiceImpl implements NotificationService {
    private final FcmDomainService fcmDomainService;
    private final FcmRedisTemplate fcmRedisTemplate;

    @Override
    @Transactional
    public void register(Long memberId, String token) {
        String t = FcmTokenUtil.parseFcmToken(token);
        String fcmToken = fcmDomainService.register(memberId, t);

        fcmRedisTemplate.saveToken(memberId, fcmToken);
    }

    @Override
    @Transactional
    public void deregister(Long memberId) {
        fcmDomainService.deregister(memberId);

        fcmRedisTemplate.deleteToken(memberId);
    }

    @Override
    public void push(Long userId, String title, long diffHours, long diffMinutes) {
        fcmRedisTemplate.getToken(userId).ifPresentOrElse(
            t -> {
                String message = fcmDomainService.getMessage(title, diffHours, diffMinutes);

                fcmDomainService.sendFCM(t, message);
                },
                () -> log.info("[FCM_APPLICATION]FCM token not found. userId={}", userId)
        );
    }
}
