package com.hp.hospin.hospital.domain.port;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;

import java.util.Optional;

public interface HospitalGradeRepository {
    Optional<JpaHospitalGradeEntity> findByHospitalCode(String hospitalCode);
}
