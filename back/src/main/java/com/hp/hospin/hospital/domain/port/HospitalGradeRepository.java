package com.hp.hospin.hospital.domain.port;

import com.hp.hospin.hospital.infrastructure.entity.HospitalGrade;

import java.util.Optional;

public interface HospitalGradeRepository {
    Optional<HospitalGrade> findByHospitalCode(String hospitalCode);
}
