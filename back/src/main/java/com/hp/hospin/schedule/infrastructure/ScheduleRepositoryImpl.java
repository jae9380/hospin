package com.hp.hospin.schedule.infrastructure;

import com.hp.hospin.global.standard.annotations.Monitored;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.domain.port.ScheduleRepository;
import com.hp.hospin.schedule.exception.ScheduleException;
import com.hp.hospin.schedule.infrastructure.entity.JpaScheduleEntity;
import com.hp.hospin.schedule.infrastructure.jpa.ScheduleJpaRepository;
import com.hp.hospin.schedule.infrastructure.mapper.SchedulePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {
    private final ScheduleJpaRepository scheduleJpaRepository;
    private final SchedulePersistenceMapper mapper;

    @Override
    @Monitored(
            domain = "schedule",
            layer = "infrastructure",
            api = "getClosestSchedules"
    )
    public Optional<List<Schedule>> getClosestSchedules(Long memberId, LocalDateTime start, LocalDateTime end) {
        return Optional.of(
                scheduleJpaRepository.findTop6ByMemberIdAndStartDatetimeBetweenOrderByStartDatetimeDesc(memberId, start, end).stream()
                        .map(mapper::jpaToDomain)
                        .toList()
        );
    }
    
    @Override
    @Monitored(
            domain = "schedule",
            layer = "infrastructure",
            api = "save"
    )
    public void save(Schedule schedule) {
        scheduleJpaRepository.save(mapper.domainToJpa(schedule));

    }

    @Override
    @Monitored(
            domain = "schedule",
            layer = "infrastructure",
            api = "getSchedule"
    )
    public Optional<Schedule> getSchedule(Long scheduleId) {
        return scheduleJpaRepository.findById(scheduleId)
                .map(mapper::jpaToDomain);
    }

    @Override
    @Monitored(
            domain = "schedule",
            layer = "infrastructure",
            api = "delete"
    )
    public void delete(Schedule schedule) {
        scheduleJpaRepository.delete(mapper.domainToJpa(schedule));
    }

    @Override
    @Monitored(
            domain = "schedule",
            layer = "infrastructure",
            api = "findByMemberId"
    )
    public Optional<List<Schedule>> findByMemberId(Long memberId) {
        return Optional.of(
                scheduleJpaRepository.findByMemberId(memberId).stream()
                .map(mapper::jpaToDomain)
                .toList()
        );
    }
}
