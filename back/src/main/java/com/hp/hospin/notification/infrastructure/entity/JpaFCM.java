package com.hp.hospin.notification.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity(name="FCM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcm",
        indexes = {@Index(name = "idx_member_token", columnList = "member_id, token")})
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JpaFCM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 512)
    private String token;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
