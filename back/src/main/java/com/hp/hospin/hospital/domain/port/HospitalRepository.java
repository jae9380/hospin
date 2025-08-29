package com.hp.hospin.hospital.domain.port;

import com.hp.hospin.hospital.infrastructure.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository {
    List<Hospital> getAllData();
    Optional<Hospital> findByHospitalCode(String hospitalCode);
    List<Hospital> findHospitalsByLatLngInt(int latInt, int lngInt);
    Page<Hospital> search(Specification<Hospital> spec, Pageable pageable);
}
