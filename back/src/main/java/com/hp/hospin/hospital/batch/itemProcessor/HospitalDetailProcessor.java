package com.hp.hospin.hospital.batch.itemProcessor;

import com.hp.hospin.hospital.infrastructure.entity.Hospital;
import com.hp.hospin.hospital.infrastructure.entity.HospitalDetail;
import com.hp.hospin.hospital.batch.dto.HospitalDetailRegister;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalDetailProcessor implements ItemProcessor<HospitalDetailRegister, HospitalDetail> {
    private final HospitalJPARepository hospitalJPARepository;

    @Override
    public HospitalDetail process(HospitalDetailRegister item) throws Exception {
        try {
            validationData(item.hospitalCode());
            return item.to();
        }catch (RuntimeException e) {
            // 존재하지 않는 Hospital Code가 있을 경우 해당 데이터는 Drop
            log.error("등록되지 않은 HospitalCode"+item.hospitalCode());
            return null;
        }
    }

    private Hospital validationData(String hospitalCode) {
        Optional<Hospital> entity = hospitalJPARepository.findByHospitalCode(hospitalCode);
        return entity.orElseThrow(RuntimeException::new);
    }
}
