package com.hp.hospin.batch.itemProcessor;

import com.hp.hospin.batch.dto.HospitalCodeWithDepartments;
import com.hp.hospin.batch.listener.cache.HospitalCodeCache;
import com.hp.hospin.batch.listener.cache.HospitalDetailCache;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.mapper.HospitalPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalDepartmentProcessor implements ItemProcessor<HospitalCodeWithDepartments, JpaHospitalDetailEntity>, StepExecutionListener {
    private final HospitalDetailCache detailCache;
    private final HospitalCodeCache hospitalCodeCache;
    private final HospitalPersistenceMapper mapper;

    @Override
    public JpaHospitalDetailEntity process(HospitalCodeWithDepartments item) throws Exception {
        Optional<JpaHospitalDetailEntity> fromCache = detailCache.get(item.getHospitalCode())
                .map(mapper::toJpaEntity);

        if (fromCache.isPresent()) {
            JpaHospitalDetailEntity entity = fromCache.get();
            entity.setDepartmentCodes(item.getDepartmentCodes());
            return entity;
        }

        if (hospitalCodeCache.contains(item.getHospitalCode())) {
            JpaHospitalDetailEntity newEntity = new JpaHospitalDetailEntity();
            newEntity.setHospitalCode(item.getHospitalCode());
            newEntity.setDepartmentCodes(item.getDepartmentCodes());
            return newEntity;
        }

        log.warn("병원 테이블에 존재하지 않는 HospitalCode: {}", item.getHospitalCode());
        return null;
    }
}