package com.hp.hospin.hospital.infrastructure.repository.jpa;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalDetailJPARepository extends JpaRepository<JpaHospitalDetailEntity, String> {
    Optional<JpaHospitalDetailEntity> findByHospitalCode(String hospitalCode);
}
