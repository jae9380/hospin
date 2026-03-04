package com.hp.hospin.member.domain.port;

import java.util.Optional;

public interface MailAuthRedisTemplate {
    void save(String key, String authCode);
    void update(String key, String authCode);
    void delete(String key);
    Optional<String> find(String key);
}
