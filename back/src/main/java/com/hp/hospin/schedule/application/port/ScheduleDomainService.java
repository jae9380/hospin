package com.hp.hospin.schedule.application.port;

import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.presentation.dto.CreateScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.UpdateScheduleRequest;

public interface ScheduleDomainService {
    Schedule createSchedule(Long userId, CreateScheduleRequest request);
    void modifySchedule(Schedule schedule, UpdateScheduleRequest updateScheduleRequest, Long userId);
    void deleteSchedule(Schedule schedule, Long userId);
}
