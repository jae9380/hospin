package com.hp.hospin.hospital.domain.port;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;

import java.util.Optional;

public interface HospitalDetailRepository {
    Optional<JpaHospitalDetailEntity> findByHospitalCode(String hospitalCode);
}
