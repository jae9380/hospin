package com.hp.hospin.schedule.application;

import com.hp.hospin.schedule.application.port.ScheduleDomainService;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.presentation.dto.CreateScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.UpdateScheduleRequest;
import com.hp.hospin.schedule.presentation.port.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleDomainService scheduleDomainService;

    @Override
    @Transactional
    public void createSchedule(Long userId, CreateScheduleRequest request) {
        validDateRange(request.getStartDatetime(), request.getEndDatetime());
        scheduleDomainService.createSchedule(userId, request);
    }

    @Override
    @Transactional
    public void modifySchedule(Long userId, Long scheduleId, UpdateScheduleRequest updateScheduleRequest) {
        scheduleDomainService.modifySchedule(scheduleId, updateScheduleRequest, userId);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        scheduleDomainService.deleteSchedule(scheduleId, userId);
    }

    @Override
    public List<Schedule> getScheduleList(Long userId) {
        List<Schedule> schedules = scheduleDomainService.getScheduleList(userId);
        // TODO: 나중에 추가 조건 생성 로직 (예: 날짜 범위, 카테고리, 반복 여부 등)
        return schedules;
    }

    private void validDateRange(LocalDateTime start, LocalDateTime end) {
        //TODO: 커스텀 예외 변경
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("시작일자는 종료일자보다 이후일 수 없습니다.");
        }
    }
}
