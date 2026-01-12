package com.hp.hospin.schedule.domain.entity;

import com.hp.hospin.schedule.domain.form.ScheduleForm;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Schedule {
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

    private Schedule(Long id, Long userId, int category, int type,
                     String title, String memo, LocalDateTime startDatetime, LocalDateTime endDatetime,
                     int recurringType, Long recurrenceRule, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public static Schedule create(Long userId, ScheduleForm request) {
        return Schedule.builder()
                .id(null)
                .userId(userId)
                .category(request.getCategory())
                .type(request.getType())
                .title(request.getTitle())
                .memo(request.getMemo())
                .startDatetime(request.getStartDatetime())
                .endDatetime(request.getEndDatetime())
                .recurringType(request.getRecurringType())
                .recurrenceRule(request.getRecurrenceRule())
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();
    }

    public Schedule update(ScheduleForm request) {
        this.category = request.getCategory();
        this.type = request.getType();
        this.title = request.getTitle();
        this.memo = request.getMemo();
        this.startDatetime = request.getStartDatetime();
        this.endDatetime = request.getEndDatetime();
        this.recurringType = request.getRecurringType();
        this.recurrenceRule = request.getRecurrenceRule();
        this.updatedAt = LocalDateTime.now();

        return this;
    }
}
