package com.hp.hospin.hospital.infrastructure.repository.jpa;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalGradeJPARepository extends JpaRepository<JpaHospitalGradeEntity, String> {
    Optional<JpaHospitalGradeEntity> findByHospitalCode(String hospitalCode);
}
