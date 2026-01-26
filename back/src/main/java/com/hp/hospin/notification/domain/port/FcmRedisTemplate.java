package com.hp.hospin.notification.domain.port;

import java.util.Optional;

public interface FcmRedisTemplate {
    void saveToken(Long userId, String token) ;
    void updateToken(Long userId, String token) ;
    void deleteToken(Long userId);
    Optional<String> getToken(Long userId) ;
}
