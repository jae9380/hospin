package com.hp.hospin.schedule.application.port;

import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.domain.form.ScheduleForm;

import java.util.List;

public interface ScheduleDomainService {
    Schedule createSchedule(Long userId, ScheduleForm request);
    Schedule modifySchedule(Long scheduleId, ScheduleForm updateScheduleRequest, Long userId);
    void deleteSchedule(Long scheduleId, Long userId);
    List<Schedule> getScheduleList(Long userId);
}
