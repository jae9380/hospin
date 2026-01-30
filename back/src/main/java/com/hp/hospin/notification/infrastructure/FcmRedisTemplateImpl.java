package com.hp.hospin.notification.infrastructure;

import com.hp.hospin.notification.domain.port.FcmRedisTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmRedisTemplateImpl implements FcmRedisTemplate {
    private static final Duration TTL = Duration.ofDays(30);
    private final StringRedisTemplate stringRedisTemplate;
    private static final String FCM_KEY_PREFIX = "FCM:";

    @Override
    public void saveToken(Long userId, String token) {
        stringRedisTemplate.opsForValue().set(FCM_KEY_PREFIX + userId, token, TTL);
    }


    @Override
    public void updateToken(Long userId, String token) {
        stringRedisTemplate.opsForValue().set(FCM_KEY_PREFIX + userId, token, TTL);
    }

    @Override
    public void deleteToken(Long userId) {
        stringRedisTemplate.delete(FCM_KEY_PREFIX + userId);
    }

    @Override
    public Optional<String> getToken(Long userId) {
        return Optional.of(stringRedisTemplate.opsForValue().get(FCM_KEY_PREFIX + userId));
    }
}
