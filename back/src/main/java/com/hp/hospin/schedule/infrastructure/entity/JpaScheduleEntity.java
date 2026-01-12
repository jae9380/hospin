package com.hp.hospin.schedule.infrastructure.entity;

import com.hp.hospin.schedule.infrastructure.entity.type.RecurringType;
import com.hp.hospin.schedule.infrastructure.entity.type.ScheduleCategoryType;
import com.hp.hospin.schedule.infrastructure.entity.type.ScheduleType;
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

    private Long userId;

    @Enumerated(EnumType.STRING)
    private ScheduleCategoryType category;

    @Enumerated(EnumType.STRING)
    private ScheduleType type;

    private String title;
    private String memo;

    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;

    // 반복 일정용
    @Enumerated(EnumType.STRING)
    private RecurringType recurringType;

    private Long recurrenceRule;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
