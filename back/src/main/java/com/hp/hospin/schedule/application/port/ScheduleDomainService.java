package com.hp.hospin.schedule.application.port;

import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.presentation.dto.CreateScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.UpdateScheduleRequest;

import java.util.List;

public interface ScheduleDomainService {
    void createSchedule(Long userId, CreateScheduleRequest request);
    void modifySchedule(Long scheduleId, UpdateScheduleRequest updateScheduleRequest, Long userId);
    void deleteSchedule(Long scheduleId, Long userId);
    List<Schedule> getScheduleList(Long userId);
}
