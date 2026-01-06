package com.hp.hospin.schedule.domain;

import com.hp.hospin.schedule.application.port.ScheduleDomainService;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.domain.port.ScheduleRepository;
import com.hp.hospin.schedule.exception.ScheduleException.*;
import com.hp.hospin.schedule.presentation.dto.CreateScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.UpdateScheduleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO: 검증 로직 분리 검토 해야 함
@Service
@RequiredArgsConstructor
public class ScheduleDomainServiceImpl implements ScheduleDomainService {
    private final ScheduleRepository scheduleRepository;

    @Override
    public void createSchedule(Long userId, CreateScheduleRequest request) {
        Schedule newSchedule = Schedule.create(userId, request);
        scheduleRepository.save(newSchedule);
    }

    @Override
    public void modifySchedule(Long scheduleId, UpdateScheduleRequest updateScheduleRequest, Long userId) {
        Schedule schedule = getSchedule(scheduleId);
        // NOTE: 검증 로직
        if (!schedule.getUserId().equals(userId)) throw new NoPerissionException();

        schedule.update(updateScheduleRequest);
    }

    @Override
    public void deleteSchedule(Long scheduleId, Long userId) {
        // NOTE: 검증 로직
        Schedule target = getSchedule(scheduleId);
        if (!target.getUserId().equals(userId)) throw new NoPerissionException();

        scheduleRepository.delete(target);
    }

    @Override
    public List<Schedule> getScheduleList(Long userId) {
        return scheduleRepository.findByUserId(userId).orElse(List.of());
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.getSchedule(scheduleId)
                .orElseThrow(ScheduleNotExistException::new);
    }
}
