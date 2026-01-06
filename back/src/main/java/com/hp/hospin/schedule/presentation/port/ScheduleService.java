package com.hp.hospin.schedule.presentation.port;

import com.hp.hospin.schedule.application.dto.ScheduleDTO;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    ScheduleDTO createSchedule(Long userId, ScheduleDTO request);
    ScheduleDTO modifySchedule(Long id, Long scheduleId, ScheduleDTO updateScheduleRequest);
    void deleteSchedule(Long id, Long scheduleId);
    List<ScheduleDTO> getScheduleList(Long userId);
}
