package com.hp.hospin.hospital.infrastructure.repository;

import com.hp.hospin.hospital.domain.port.HospitalRepository;
import com.hp.hospin.hospital.infrastructure.entity.Hospital;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HospitalRepositoryImpl implements HospitalRepository {
    private final HospitalJPARepository hospitalJPARepository;

    @Override
    public List<Hospital> getAllData() {
        return hospitalJPARepository.findAll();
    }

    @Override
    public Optional<Hospital> findByHospitalCode(String hospitalCode) {
        return hospitalJPARepository.findByHospitalCode(hospitalCode);
    }

    @Override
    public List<Hospital> findHospitalsByLatLngInt(int latInt, int lngInt) {
        return hospitalJPARepository.findHospitalsByLatLngInt(latInt, lngInt);
    }

    @Override
    public Page<Hospital> search(Specification<Hospital> spec, Pageable pageable) {
        return hospitalJPARepository.findAll(spec, pageable);
    }
}
