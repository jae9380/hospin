package com.hp.hospin.schedule.batch.itemWriter;

import com.hp.hospin.notification.domain.port.FCMSender;
import com.hp.hospin.notification.persentation.port.NotificationService;
import com.hp.hospin.schedule.domain.entity.Schedule;
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
    private final NotificationService notificationService;

    @Override
    public void write(Chunk<? extends Schedule> chunk) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        for (Schedule schedule : chunk) {
            LocalDateTime start = schedule.getStartDatetime()
                    .truncatedTo(ChronoUnit.MINUTES);

            long diffMinutes = Duration.between(now, start).toMinutes();
            long diffHours = diffMinutes / 60;

            // 이미 지난 일정 방어
            if (diffMinutes <= 0) {
                continue;
            }

            if (isTargetHour(diffHours, diffMinutes)) {
                notificationService.push(schedule.getMemberId(), schedule.getTitle(), diffHours, diffMinutes);
            }
        }
    }

//    NOTE: 각 24, 6, 3, 1 초과 또는 미만일 경우 Push X
    private boolean isTargetHour(long diffHours, long diffMinutes) {
        return diffMinutes % 60 == 0 && (
                diffHours == 24 ||
                        diffHours == 6 ||
                        diffHours == 3 ||
                        diffHours == 1
        );
    }
}