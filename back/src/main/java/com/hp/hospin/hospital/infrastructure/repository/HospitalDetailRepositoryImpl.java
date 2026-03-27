package com.hp.hospin.hospital.infrastructure.repository;

import com.hp.hospin.global.standard.annotations.Monitored;
import com.hp.hospin.hospital.domain.port.HospitalDetailRepository;
import com.hp.hospin.hospital.domain.type.HospitalDetail;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.mapper.HospitalPersistenceMapper;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalDetailJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HospitalDetailRepositoryImpl implements HospitalDetailRepository {
    private final HospitalDetailJPARepository hospitalDetailJPARepository;
    private final HospitalPersistenceMapper mapper;

    @Override
    @Monitored(
            domain = "hospital",
            layer = "infrastructure",
            api = "findByHospitalCode(detail)"
    )
    public Optional<HospitalDetail> findByHospitalCode(String hospitalCode) {
        return hospitalDetailJPARepository.findByHospitalCode(hospitalCode)
                .map(mapper::toDomain);
    }
}
