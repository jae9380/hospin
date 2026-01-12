package com.hp.hospin.schedule.domain.form;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleForm {
    private final int category;
    private final int type;
    private final String title;
    private final String memo;
    private final LocalDateTime startDatetime;
    private final LocalDateTime endDatetime;
    private final int recurringType;
    private final Long recurrenceRule;

    public ScheduleForm(int category, int type, String title, String memo, LocalDateTime startDatetime, LocalDateTime endDatetime, int recurringType, Long recurrenceRule) {
        this.category = category;
        this.type = type;
        this.title = title;
        this.memo = memo;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.recurringType = recurringType;
        this.recurrenceRule = recurrenceRule;
    }
}
