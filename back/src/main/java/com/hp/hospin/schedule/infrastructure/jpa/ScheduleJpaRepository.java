package com.hp.hospin.schedule.infrastructure.jpa;

import com.hp.hospin.schedule.infrastructure.entity.JpaScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ScheduleJpaRepository extends JpaRepository<JpaScheduleEntity, Long> {
    List<JpaScheduleEntity> findByMemberId(Long memberId);
    List<JpaScheduleEntity> findTop6ByMemberIdAndStartDatetimeBetweenOrderByStartDatetimeDesc(Long memberId, LocalDateTime start, LocalDateTime end);
}
