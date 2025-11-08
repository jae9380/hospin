package com.hp.hospin.member.application.port;

import com.hp.hospin.member.infrastructure.entity.RefreshToken;

public interface TokenDomainService {
    void saveOrReplace(Long userId, String newRefreshToken);
}
