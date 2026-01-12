package com.hp.hospin.schedule.application.dto;

import com.hp.hospin.schedule.presentation.dto.ScheduleRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleDTO {

    private Long id;
    private Long userId;
    private int category;
    private int type;
    private String title;
    private String memo;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private int recurringType;
    private Long recurrenceRule;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ScheduleDTO(Long id, Long userId, int category, int type, String title, String memo,
                       LocalDateTime startDatetime, LocalDateTime endDatetime, int recurringType,
                       Long recurrenceRule, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.type = type;
        this.title = title;
        this.memo = memo;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.recurringType = recurringType;
        this.recurrenceRule = recurrenceRule;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ScheduleDTO(ScheduleRequest request) {
        this.id = null;
        this.userId = null;
        this.category = request.getCategory();
        this.type = request.getType();
        this.title = request.getTitle();
        this.memo = request.getMemo();
        this.startDatetime = request.getStartDatetime();
        this.endDatetime = request.getEndDatetime();
        this.recurringType = request.getRecurringType();
        this.recurrenceRule = request.getRecurrenceRule();
        this.createdAt = null;
        this.updatedAt = null;
    }
}
