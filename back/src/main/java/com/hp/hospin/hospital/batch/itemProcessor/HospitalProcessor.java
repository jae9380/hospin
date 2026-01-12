package com.hp.hospin.hospital.batch.itemProcessor;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import com.hp.hospin.hospital.batch.dto.HospitalRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HospitalProcessor implements ItemProcessor<HospitalRegister, JpaHospitalEntity> {

    @Override
    public JpaHospitalEntity process(HospitalRegister item) throws Exception {
        return item.to();
    }
}
