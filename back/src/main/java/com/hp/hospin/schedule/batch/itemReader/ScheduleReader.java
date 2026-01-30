package com.hp.hospin.schedule.batch.itemReader;

import com.hp.hospin.schedule.infrastructure.entity.JpaScheduleEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleReader {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public JpaPagingItemReader<JpaScheduleEntity> scheduleItemReader() {
        return new JpaPagingItemReaderBuilder<JpaScheduleEntity>()
                .name("scheduleItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString(
                        "SELECT s FROM Schedule s " +
                                "WHERE s.startDatetime BETWEEN :now AND :next24h " +
                                "AND (" +
                                "   (s.notifyHours IS NULL AND :diff24 = 24) OR " +
                                "   (s.notifyHours = 24 AND :diff6 = 6) OR " +
                                "   (s.notifyHours = 6 AND :diff3 = 3) OR " +
                                "   (s.notifyHours = 3 AND :diff1 = 1)" +
                                ")"
                )
                .parameterValues(Map.of(
                        "now", LocalDateTime.now(),
                        "next24h", LocalDateTime.now().plusHours(24),
                        "diff24", 24,
                        "diff6", 6,
                        "diff3", 3,
                        "diff1", 1
                ))
                .pageSize(1000)
                .build();
    }
}
