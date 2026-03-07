package com.hp.hospin.batch.itemProcessor;

import com.hp.hospin.batch.dto.HospitalCodeWithDepartments;
import com.hp.hospin.batch.listener.cache.HospitalDetailCache;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.mapper.HospitalPersistenceMapper;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalDetailJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalDepartmentProcessor implements ItemProcessor<HospitalCodeWithDepartments, JpaHospitalDetailEntity>, StepExecutionListener{
    private final HospitalDetailCache cache;
    private final HospitalPersistenceMapper mapper;

    @Override
    public JpaHospitalDetailEntity process(HospitalCodeWithDepartments item) throws Exception {
        try {
            JpaHospitalDetailEntity entity = mapper.toJpaEntity(cache.get(item.getHospitalCode()).orElseThrow(RuntimeException::new));
            entity.setDepartmentCodes(item.getDepartmentCodes());
            return entity;
        }catch (RuntimeException e) {
            log.error("등록되지 않은 HospitalCode"+item.getHospitalCode());
            return null;
        }
    }
}