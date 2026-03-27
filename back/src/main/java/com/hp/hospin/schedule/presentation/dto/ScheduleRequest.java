package com.hp.hospin.schedule.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleRequest {
    private int category;
    private String title;
    private String memo;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
}
