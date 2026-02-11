package com.hp.hospin.batch.itemProcessor;

import com.hp.hospin.notification.persentation.port.NotificationService;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.infrastructure.entity.JpaScheduleEntity;
import com.hp.hospin.schedule.infrastructure.mapper.SchedulePersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleProcessor implements ItemProcessor<JpaScheduleEntity, Schedule> {
    private final NotificationService notificationService;
    private final SchedulePersistenceMapper mapper;
    @Override
    public Schedule process(JpaScheduleEntity item) throws Exception {
        Schedule schedule = mapper.jpaToDomain(item);

//        test
//        if (schedule.getId() != null || schedule.getId() == 1L) {
//            throw new RuntimeException("TEST_PROCESSOR_FAILURE");
//        }
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        LocalDateTime start = schedule.getStartDatetime()
                .truncatedTo(ChronoUnit.MINUTES);

        long diffMinutes = Duration.between(now, start).toMinutes();
        long diffHours = diffMinutes / 60;

            // 이미 지난 일정 방어
        if (diffMinutes <= 0) return null;

        // 알림 대상 시간인지 확인
        if (isTargetHour(diffHours, diffMinutes, schedule.getNotifyHours())) {
//            if (true) {
            // FCM 발송
            notificationService.push(schedule.getMemberId(), schedule.getTitle(), diffHours, diffMinutes);

            schedule.updateNotifyHours(diffHours);

            log.info("[SCHEDULE_BATCH] Notification sent for scheduleId={}, memberId={}, notifyHours={}",
                    schedule.getId(), schedule.getMemberId(), diffHours);
        }

        return schedule; // Processor에서는 DB 업데이트는 하지 않음
    }

    /**
     * 알림 대상 여부 판단
     * schedule.getNotifyHours()가 이미 발송된 시간이라면 스킵
     */
    private boolean isTargetHour(long diffHours, long diffMinutes, Long notifyHours) {
        if (diffMinutes % 60 != 0) return false; // 한 시간 단위가 아니면 스킵
        if (notifyHours != null && notifyHours >= diffHours) return false; // 이미 알림 발송했으면 스킵

        return diffHours == 24 || diffHours == 6 || diffHours == 3 || diffHours == 1;
    }
}
