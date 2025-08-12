package com.hp.hospin.hospital.repository;

import com.hp.hospin.hospital.entity.HospitalDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalDetailRepository extends JpaRepository<HospitalDetail, String> {
    Optional<HospitalDetail> findByHospitalCode(String hospitalCode);
}
