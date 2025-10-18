package com.hp.hospin.schedule.domain;

import com.hp.hospin.schedule.application.port.ScheduleDomainService;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.exception.ScheduleException.*;
import com.hp.hospin.schedule.presentation.dto.CreateScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.UpdateScheduleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TODO: 검증 로직 분리 검토 해야 함
@Service
@RequiredArgsConstructor
public class ScheduleDomainServiceImpl implements ScheduleDomainService {
    @Override
    public Schedule createSchedule(Long userId, CreateScheduleRequest request) {
        return Schedule.create(userId, request);
    }

    @Override
    public void modifySchedule(Schedule schedule, UpdateScheduleRequest updateScheduleRequest, Long userId) {
        // NOTE: 검증 로직
        if (!schedule.getUserId().equals(userId)) throw new NoPerissionException();

        schedule.update(updateScheduleRequest);
    }

    @Override
    public void deleteSchedule(Schedule schedule, Long userId) {
        // NOTE: 검증 로직
        if (!schedule.getUserId().equals(userId)) throw new NoPerissionException();
    }
}
