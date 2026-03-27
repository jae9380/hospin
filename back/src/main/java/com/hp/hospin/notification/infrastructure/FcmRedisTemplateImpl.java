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
    private static final String FCM_KEY_PREFIX = "FCM:TOKEN:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(Long userId, String token) {
        stringRedisTemplate.opsForValue().set(generateKey(userId), token, TTL);
    }


    @Override
    public void update(Long userId, String token) {
        stringRedisTemplate.opsForValue().set(generateKey(userId), token, TTL);
    }

    @Override
    public void delete(Long userId) {
        stringRedisTemplate.delete(generateKey(userId));
    }

    @Override
    public Optional<String> find(Long userId) {
        return Optional.ofNullable(
                stringRedisTemplate.opsForValue().get(generateKey(userId))
        );
    }

    private String generateKey(Long userId) {
        return FCM_KEY_PREFIX + userId;
    }
}
