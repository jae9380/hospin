package com.hp.hospin.hospital.infrastructure.repository;

import com.hp.hospin.hospital.domain.port.HospitalDetailRepository;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalDetailJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HospitalDetailRepositoryImpl implements HospitalDetailRepository {
    private final HospitalDetailJPARepository hospitalDetailJPARepository;
    @Override
    public Optional<JpaHospitalDetailEntity> findByHospitalCode(String hospitalCode) {
        return hospitalDetailJPARepository.findByHospitalCode(hospitalCode);
    }
}
