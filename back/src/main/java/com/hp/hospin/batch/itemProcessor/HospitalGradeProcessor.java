package com.hp.hospin.batch.itemProcessor;

import com.hp.hospin.batch.dto.HospitalGradeRegister;
import com.hp.hospin.batch.listener.cache.HospitalCodeCache;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalGradeProcessor implements ItemProcessor<HospitalGradeRegister, JpaHospitalGradeEntity> {
    private final HospitalJPARepository hospitalJPARepository;
    private final HospitalCodeCache hospitalCodeCache;

    @Override
    public JpaHospitalGradeEntity process(HospitalGradeRegister item) throws Exception {

        if (!hospitalCodeCache.contains(item.getHospitalCode())) {
            log.warn("등록되지 않은 HospitalCode: {}", item.getHospitalCode());
            return null;
        }
        return item.to();
    }
}
