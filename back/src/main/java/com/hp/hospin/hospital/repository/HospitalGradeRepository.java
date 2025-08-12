package com.hp.hospin.hospital.repository;

import com.hp.hospin.hospital.entity.HospitalGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalGradeRepository extends JpaRepository<HospitalGrade, String> {
}
