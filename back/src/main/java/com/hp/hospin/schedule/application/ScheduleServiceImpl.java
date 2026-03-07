package com.hp.hospin.schedule.application;

import com.hp.hospin.global.standard.annotations.Monitored;
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
    @Monitored(
            domain = "schedule",
            layer = "application",
            api = "createSchedule"
    )
    @Transactional
    public ScheduleDTO createSchedule(Long memberId, ScheduleDTO createRequest) {
        validDateRange(createRequest.getStartDatetime(), createRequest.getEndDatetime());
        return mapper.toDto(scheduleDomainService.createSchedule(memberId, mapper.toForm(createRequest)));
    }

    @Override
    @Monitored(
            domain = "schedule",
            layer = "application",
            api = "modifySchedule"
    )
    @Transactional
    public ScheduleDTO modifySchedule(Long memberId, Long scheduleId, ScheduleDTO updateRequest) {
        return mapper.toDto(scheduleDomainService.modifySchedule(scheduleId, mapper.toForm(updateRequest), memberId));
    }

    @Override
    @Monitored(
            domain = "schedule",
            layer = "application",
            api = "deleteSchedule"
    )
    @Transactional
    public void deleteSchedule(Long memberId, Long scheduleId) {
        scheduleDomainService.deleteSchedule(scheduleId, memberId);
    }

    @Override
    @Monitored(
            domain = "schedule",
            layer = "application",
            api = "getScheduleList"
    )
    public List<ScheduleDTO> getScheduleList(Long memberId) {
        List<ScheduleDTO> schedules = scheduleDomainService.getScheduleList(memberId).stream().map(mapper::toDto).toList();
        // TODO: 나중에 추가 조건 생성 로직 (예: 날짜 범위, 카테고리, 반복 여부 등)
        return schedules;
    }

    @Override
    @Monitored(
            domain = "schedule",
            layer = "application",
            api = "getClosestSchedule"
    )
    public List<ScheduleDTO> getClosestSchedule(Long memberId) {
        List<ScheduleDTO> schedules = scheduleDomainService.getClosestSchedule(memberId).stream().map(mapper::toDto).toList();

        return schedules;
    }

    private void validDateRange(LocalDateTime start, LocalDateTime end) {
        //TODO: 커스텀 예외 변경
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("시작일자는 종료일자보다 이후일 수 없습니다.");
        }
    }
}
