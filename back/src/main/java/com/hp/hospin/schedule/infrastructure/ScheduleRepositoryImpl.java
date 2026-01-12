package com.hp.hospin.schedule.infrastructure;

import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.domain.port.ScheduleRepository;
import com.hp.hospin.schedule.exception.ScheduleException;
import com.hp.hospin.schedule.infrastructure.entity.JpaScheduleEntity;
import com.hp.hospin.schedule.infrastructure.jpa.ScheduleJpaRepository;
import com.hp.hospin.schedule.infrastructure.mapper.SchedulePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {
    private final ScheduleJpaRepository scheduleJpaRepository;
    private final SchedulePersistenceMapper mapper;

    @Override
    public void save(Schedule schedule) {
        scheduleJpaRepository.save(mapper.domainToJpa(schedule));

    }

    @Override
    public Optional<Schedule> getSchedule(Long scheduleId) {
        return scheduleJpaRepository.findById(scheduleId)
                .map(mapper::jpaToDomain);
    }

    @Override
    public void delete(Schedule schedule) {
        scheduleJpaRepository.delete(mapper.domainToJpa(schedule));
    }

    @Override
    public Optional<List<Schedule>> findByUserId(Long userId) {
        return Optional.of(
                scheduleJpaRepository.findByUserId(userId).stream()
                .map(mapper::jpaToDomain)
                .toList()
        );
    }
}
