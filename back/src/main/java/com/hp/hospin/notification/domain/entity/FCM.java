package com.hp.hospin.notification.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FCM {
    private Long id;
    private Long memberId;
    private String token;
    private boolean active;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public static FCM create(Long memberId, String token, boolean active) {
        return FCM.builder()
                .memberId(memberId)
                .token(token)
                .active(active)
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void activate() {
        if (this.active) {
            return; // 이미 활성화 상태
        }
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (!this.active) {
            return; // 이미 비활성화 상태면 아무 것도 하지 않음
        }
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isSameToken(String otherToken) {
        return otherToken != null && otherToken.equals(this.token);
    }

    public void changeToken(String newToken) {
        if (newToken == null) {
            throw new IllegalArgumentException("FCM token must not be null");
        }
        this.token = newToken;
        this.updatedAt = LocalDateTime.now();
    }
}
