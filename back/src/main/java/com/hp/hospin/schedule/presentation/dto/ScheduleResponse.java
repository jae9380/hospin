package com.hp.hospin.schedule.presentation.dto;

import java.time.LocalDateTime;

public record ScheduleResponse(
        Long id,
        Long userId,
        int category,
        int type,
        String title,
        String memo,
        LocalDateTime startDatetime,
        LocalDateTime endDatetime,
        int recurringType,
        Long recurrenceRule,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
