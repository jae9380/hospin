package com.hp.hospin.schedule.domain.port;

import com.hp.hospin.schedule.domain.entity.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    void save(Schedule schedule);
    Optional<Schedule> getSchedule(Long scheduleId);
    void delete(Schedule schedule);
    Optional<List<Schedule>> findByUserId(Long userId);
}
