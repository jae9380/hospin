package com.hp.hospin.hospital.infrastructure.repository;

import com.hp.hospin.hospital.domain.port.HospitalGradeRepository;
import com.hp.hospin.hospital.domain.type.HospitalGrade;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;
import com.hp.hospin.hospital.infrastructure.mapper.HospitalPersistenceMapper;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalGradeJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HospitalGradeRepositoryImpl implements HospitalGradeRepository {
    private final HospitalGradeJPARepository hospitalGradeJPARepository;
    private final HospitalPersistenceMapper mapper;

    @Override
    public Optional<HospitalGrade> findByHospitalCode(String hospitalCode) {
        return hospitalGradeJPARepository.findByHospitalCode(hospitalCode)
                .map(mapper::toDomain);
    }
}
