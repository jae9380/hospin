package com.hp.hospin.hospital.infrastructure.repository.jpa;

import com.hp.hospin.hospital.infrastructure.entity.HospitalGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalGradeJPARepository extends JpaRepository<HospitalGrade, String> {
    Optional<HospitalGrade> findByHospitalCode(String hospitalCode);
}
