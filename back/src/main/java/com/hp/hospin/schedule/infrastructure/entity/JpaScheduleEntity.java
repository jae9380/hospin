package com.hp.hospin.schedule.infrastructure.entity;

import com.hp.hospin.schedule.infrastructure.entity.type.ScheduleCategoryType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity(name="Schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Schedule")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JpaScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    @Enumerated(EnumType.STRING)
    private ScheduleCategoryType category;
    private String title;
    private String memo;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Long notifyHours; // 마지막으로 알림 보낸 시점 기준 남은 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
