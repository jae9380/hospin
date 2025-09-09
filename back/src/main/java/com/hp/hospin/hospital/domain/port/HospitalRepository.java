package com.hp.hospin.hospital.domain.port;

import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalSearchCriteria;
import com.hp.hospin.hospital.domain.type.PageResult;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository {
    List<Hospital> getAllData();
    Optional<Hospital> findByHospitalCode(String hospitalCode);
    List<Hospital> findHospitalsByLatLngInt(int latInt, int lngInt);
//    Page<JpaHospitalEntity> search(Specification<JpaHospitalEntity> spec, Pageable pageable);
    PageResult<Hospital> search(HospitalSearchCriteria query, int page, int size);
}
