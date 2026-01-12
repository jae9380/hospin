package com.hp.hospin.hospital.domain.port;

import com.hp.hospin.hospital.domain.type.HospitalGrade;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;

import java.util.Optional;

public interface HospitalGradeRepository {
    Optional<HospitalGrade> findByHospitalCode(String hospitalCode);
}
