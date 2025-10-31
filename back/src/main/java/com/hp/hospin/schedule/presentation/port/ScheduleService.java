package com.hp.hospin.schedule.presentation.port;

import com.hp.hospin.global.common.MemberDetails;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.presentation.dto.CreateScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.ScheduleResponse;
import com.hp.hospin.schedule.presentation.dto.UpdateScheduleRequest;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    void createSchedule(Long userId, CreateScheduleRequest request);
    void modifySchedule(Long id, Long scheduleId, UpdateScheduleRequest updateScheduleRequest);
    void deleteSchedule(Long id, Long scheduleId);
    List<Schedule> getScheduleList(Long userId);
}
