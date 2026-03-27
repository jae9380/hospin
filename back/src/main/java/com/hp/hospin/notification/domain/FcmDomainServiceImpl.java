package com.hp.hospin.notification.domain;

import com.hp.hospin.notification.application.port.FcmDomainService;
import com.hp.hospin.notification.domain.entity.FCM;
import com.hp.hospin.notification.domain.port.FCMSender;
import com.hp.hospin.notification.domain.port.FcmRedisTemplate;
import com.hp.hospin.notification.domain.port.FcmRepository;
import com.hp.hospin.notification.exception.NotificationException.*;
import com.hp.hospin.schedule.domain.entity.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmDomainServiceImpl implements FcmDomainService {
    private final FcmRepository fcmRepository;
    private final FCMSender sender;
    @Override
    public String register(Long memberId, String token) {
        FCM fcm = fcmRepository.findFCMByUserId(memberId)
                .map(existing -> updateIfNeeded(existing, token))
                .orElseGet(() -> createNew(memberId, token));

        fcmRepository.save(fcm);

        return fcm.getToken();
    }

    @Override
    public void deregister(Long memberId) {
        FCM fcm = fcmRepository.findFCMByUserId(memberId)
                .orElseThrow(FCMNotExistException::new);

        fcm.deactivate();
        fcmRepository.save(fcm);
    }

    @Override
    public void sendFCM(String token, String message) {
        sender.send(token, "title", message);
    }

    @Override
    public String getMessage(String title, long diffHours, long diffMinutes) {
        return buildMessage(title, diffHours, diffMinutes);
    }

    private String buildMessage(String title, long diffHours, long diffMinutes) {
        return switch ((int) diffHours) {
            case 24 -> "내일 \"" + title + "\" 일정이 있습니다.";
            case 6 -> "오늘 6시간 뒤 \"" + title + "\" 일정이 있습니다.";
            case 3 -> "오늘 3시간 뒤 \"" + title + "\" 일정이 있습니다.";
            case 1 -> "1시간 뒤 \"" + title + "\" 일정이 있습니다.";
            default -> {
                long remainingMinutes = diffMinutes % 60;
                yield diffHours + "시간 " + remainingMinutes + "분 뒤 \"" + title + "\" 일정이 있습니다.";
            }
        };
    }

    private FCM updateIfNeeded(FCM fcm, String newToken) {
        if (!fcm.isSameToken(newToken)) {
            fcm.changeToken(newToken);
            log.info("FCM token updated for memberId={}", fcm.getMemberId());
        }
        fcm.activate();
        return fcm;
    }

    private FCM createNew(Long userId, String token) {
        return FCM.create(userId, token, true);
    }
}
