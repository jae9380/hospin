package com.hp.hospin.notification.domain.port;

import java.util.Optional;

public interface FcmRedisTemplate {
    void save(Long userId, String token) ;
    void update(Long userId, String token) ;
    void delete(Long userId);
    Optional<String> find(Long userId) ;
}
