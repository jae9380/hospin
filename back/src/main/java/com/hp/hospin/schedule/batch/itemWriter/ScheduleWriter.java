package com.hp.hospin.schedule.batch.itemWriter;

import com.hp.hospin.notification.domain.port.FCMSender;
import com.hp.hospin.notification.persentation.port.NotificationService;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.infrastructure.mapper.SchedulePersistenceMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleWriter implements ItemWriter<Schedule> {
    private final SchedulePersistenceMapper mapper;
    private final EntityManager entityManager;

    @Override
    public void write(Chunk<? extends Schedule> chunk) {
        chunk.forEach(schedule -> entityManager.merge(mapper.domainToJpa(schedule)));

    }

}