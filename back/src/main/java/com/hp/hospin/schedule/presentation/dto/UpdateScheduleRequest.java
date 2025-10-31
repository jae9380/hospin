package com.hp.hospin.schedule.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateScheduleRequest {
    private int category;
    private int type;
    private String title;
    private String memo;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private int recurringType;
    private Long recurrenceRule;
}
