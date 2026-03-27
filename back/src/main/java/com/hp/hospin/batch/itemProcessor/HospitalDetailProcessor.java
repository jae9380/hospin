package com.hp.hospin.batch.itemProcessor;

import com.hp.hospin.batch.dto.HospitalDetailRegister;
import com.hp.hospin.batch.listener.cache.HospitalCodeCache;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalDetailProcessor implements ItemProcessor<HospitalDetailRegister, JpaHospitalDetailEntity> {
    private final HospitalCodeCache hospitalCodeCache;

    @Override
    public JpaHospitalDetailEntity process(HospitalDetailRegister item) {
        if (!hospitalCodeCache.contains(item.hospitalCode())) {
            log.warn("등록되지 않은 HospitalCode: {}", item.hospitalCode());
            return null;
        }
        return item.to();
    }
}
