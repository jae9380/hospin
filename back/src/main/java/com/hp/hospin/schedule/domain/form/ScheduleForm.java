package com.hp.hospin.schedule.domain.form;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleForm {
    private final int category;
    private final String title;
    private final String memo;
    private final LocalDateTime startDatetime;
    private final LocalDateTime endDatetime;

    public ScheduleForm(int category, String title, String memo, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        this.category = category;
        this.title = title;
        this.memo = memo;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
    }
}
