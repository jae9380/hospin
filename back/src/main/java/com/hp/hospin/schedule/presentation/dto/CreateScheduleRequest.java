package com.hp.hospin.schedule.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateScheduleRequest {
    private Long id; // TODO: 해당 id 칼럼은 필요 없어 보인다
    private int category;
    private int type;
    private String title;
    private String memo;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private int recurringType;
    private Long recurrenceRule;
}
