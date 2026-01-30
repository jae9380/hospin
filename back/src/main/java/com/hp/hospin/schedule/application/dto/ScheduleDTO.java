package com.hp.hospin.schedule.application.dto;

import com.hp.hospin.schedule.presentation.dto.ScheduleRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleDTO {

    private Long id;
    private Long memberId;
    private int category;
    private String title;
    private String memo;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Long notifyHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ScheduleDTO(Long id, Long memberId, int category, String title, String memo,
                       LocalDateTime startDatetime, LocalDateTime endDatetime,
                       Long notifyHours, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.memberId = memberId;
        this.category = category;
        this.title = title;
        this.memo = memo;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.notifyHours = notifyHours;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ScheduleDTO(ScheduleRequest request) {
        this.id = null;
        this.memberId = null;
        this.category = request.getCategory();
        this.title = request.getTitle();
        this.memo = request.getMemo();
        this.startDatetime = request.getStartDatetime();
        this.endDatetime = request.getEndDatetime();
        this.notifyHours = null;
        this.createdAt = null;
        this.updatedAt = null;
    }
}
