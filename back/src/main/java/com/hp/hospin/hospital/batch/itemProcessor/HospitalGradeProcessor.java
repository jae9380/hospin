package com.hp.hospin.hospital.batch.itemProcessor;

import com.hp.hospin.hospital.entity.Hospital;
import com.hp.hospin.hospital.entity.HospitalGrade;
import com.hp.hospin.hospital.batch.dto.HospitalGradeRegister;
import com.hp.hospin.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalGradeProcessor implements ItemProcessor<HospitalGradeRegister, HospitalGrade> {
    private final HospitalRepository hospitalRepository;

    @Override
    public HospitalGrade process(HospitalGradeRegister item) throws Exception {
        try {
            validationData(item.getHospitalCode());
            return item.to();
        } catch (RuntimeException e) {
            // 존재하지 않는 Hospital Code가 있을 경우 해당 데이터는 Drop
            log.error("등록되지 않은 HospitalCode"+item.getHospitalCode());
            return null;
        }
    }

    private Hospital validationData(String hospitalCode) {
        Optional<Hospital> dao = hospitalRepository.findByHospitalCode(hospitalCode);
        return dao.orElseThrow(RuntimeException :: new);
    }
}
