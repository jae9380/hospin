package com.hp.hospin.hospital.batch.itemProcessor;

import com.hp.hospin.hospital.infrastructure.entity.HospitalDetail;
import com.hp.hospin.hospital.batch.dto.HospitalCodeWithDepartments;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalDetailJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalDepartmentProcessor implements ItemProcessor<HospitalCodeWithDepartments, HospitalDetail> {
    private final HospitalDetailJPARepository hospitalDetailJPARepository;

    @Override
    public HospitalDetail process(HospitalCodeWithDepartments item) throws Exception {
        try {
            HospitalDetail entity = validationData(item.getHospitalCode());
            entity.setDepartmentCodes(item.getDepartmentCodes());
            return entity;
        }catch (RuntimeException e) {
            log.error("등록되지 않은 HospitalCode"+item.getHospitalCode());
            return null;
        }
    }

    private HospitalDetail validationData(String hospitalCode) {
        Optional<HospitalDetail> entity = hospitalDetailJPARepository.findByHospitalCode(hospitalCode);
        return entity.orElseThrow(RuntimeException::new);
    }
}