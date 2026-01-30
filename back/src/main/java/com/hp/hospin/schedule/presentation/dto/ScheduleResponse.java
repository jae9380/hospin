package com.hp.hospin.schedule.presentation.dto;

import java.time.LocalDateTime;

public record ScheduleResponse(
        Long id,
        Long memberId,
        int category,
        String title,
        String memo,
        LocalDateTime startDatetime,
        LocalDateTime endDatetime,
        Long notifyHours,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
