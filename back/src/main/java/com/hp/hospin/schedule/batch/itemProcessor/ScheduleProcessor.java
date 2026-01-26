package com.hp.hospin.schedule.batch.itemProcessor;

import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.infrastructure.entity.JpaScheduleEntity;
import com.hp.hospin.schedule.infrastructure.mapper.SchedulePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleProcessor implements ItemProcessor<JpaScheduleEntity, Schedule> {
    private final SchedulePersistenceMapper mapper;
    @Override
    public Schedule process(JpaScheduleEntity item) throws Exception {
        return mapper.jpaToDomain(item);
    }
}
