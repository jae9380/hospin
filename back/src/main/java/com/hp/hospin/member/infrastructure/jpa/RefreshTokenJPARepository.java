package com.hp.hospin.member.infrastructure.jpa;

import com.hp.hospin.member.infrastructure.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenJPARepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUserId(Long userId);

    @Modifying
    @Query(value = """
            INSERT INTO refresh_token (user_id, refresh_token)
            VALUES (:userId, :refreshToken)
            ON DUPLICATE KEY UPDATE refresh_token = :refreshToken
            """, nativeQuery = true)
    void upsertByUserId(@Param("userId") Long userId, @Param("refreshToken") String refreshToken);
}