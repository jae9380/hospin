package com.hp.hospin.hospital.infrastructure.repository.jpa;

import com.hp.hospin.hospital.infrastructure.entity.HospitalDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalDetailJPARepository extends JpaRepository<HospitalDetail, String> {
    Optional<HospitalDetail> findByHospitalCode(String hospitalCode);
}
