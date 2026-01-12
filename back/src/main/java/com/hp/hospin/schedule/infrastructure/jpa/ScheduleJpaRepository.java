package com.hp.hospin.schedule.infrastructure.jpa;

import com.hp.hospin.schedule.infrastructure.entity.JpaScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleJpaRepository extends JpaRepository<JpaScheduleEntity, Long> {
    List<JpaScheduleEntity> findByUserId(Long userId);
}
