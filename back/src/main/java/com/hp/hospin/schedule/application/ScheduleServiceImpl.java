package com.hp.hospin.schedule.application;

import com.hp.hospin.schedule.application.dto.ScheduleDTO;
import com.hp.hospin.schedule.application.mapper.ScheduleDtoMapper;
import com.hp.hospin.schedule.application.port.ScheduleDomainService;
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
    private final ScheduleDtoMapper mapper;

    @Override
    @Transactional
    public ScheduleDTO createSchedule(Long userId, ScheduleDTO createRequest) {
        validDateRange(createRequest.getStartDatetime(), createRequest.getEndDatetime());
        return mapper.toDto(scheduleDomainService.createSchedule(userId, mapper.toForm(createRequest)));
    }

    @Override
    @Transactional
    public ScheduleDTO modifySchedule(Long userId, Long scheduleId, ScheduleDTO updateRequest) {
        return mapper.toDto(scheduleDomainService.modifySchedule(scheduleId, mapper.toForm(updateRequest), userId));
    }

    @Override
    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        scheduleDomainService.deleteSchedule(scheduleId, userId);
    }

    @Override
    public List<ScheduleDTO> getScheduleList(Long userId) {
        List<ScheduleDTO> schedules = scheduleDomainService.getScheduleList(userId).stream().map(mapper::toDto).toList();
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
