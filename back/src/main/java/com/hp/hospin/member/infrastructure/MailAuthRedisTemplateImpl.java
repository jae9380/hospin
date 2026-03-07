package com.hp.hospin.member.infrastructure;

import com.hp.hospin.member.domain.port.MailAuthRedisTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MailAuthRedisTemplateImpl implements MailAuthRedisTemplate {
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final String MAIL_AUTH_KEY_PREFIX = "MAIL:CODE:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(String key, String authCode) {
        stringRedisTemplate.opsForValue().set(generateKey(key), authCode, TTL);
    }

    @Override
    public void update(String key, String authCode) {
        stringRedisTemplate.opsForValue().set(generateKey(key), authCode, TTL);
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(generateKey(key));
    }

    @Override
    public Optional<String> find(String key) {
        return Optional.ofNullable(
                stringRedisTemplate.opsForValue().get(generateKey(key))
        );
    }

    private String generateKey(String userId) {
        return MAIL_AUTH_KEY_PREFIX + userId;
    }
}
