package com.hp.hospin.member.application.port;

public interface RefreshTokenDomainService {
    void saveOrReplace(Long userId, String newRefreshToken);
}
