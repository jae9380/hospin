package com.hp.hospin.hospital.domain.port;

import com.hp.hospin.hospital.domain.type.HospitalDetail;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;

import java.util.Optional;

public interface HospitalDetailRepository {
    Optional<HospitalDetail> findByHospitalCode(String hospitalCode);
}
