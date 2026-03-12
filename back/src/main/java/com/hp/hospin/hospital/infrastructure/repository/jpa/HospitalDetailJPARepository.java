package com.hp.hospin.hospital.infrastructure.repository.jpa;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface HospitalDetailJPARepository extends JpaRepository<JpaHospitalDetailEntity, String> {
    Optional<JpaHospitalDetailEntity> findByHospitalCode(String hospitalCode);

    // 배치 시작 전 진료과 코드를 초기화 — hospital_department.csv만이 유일한 출처
    @Modifying
    @Transactional
    @Query("UPDATE hospital_detail h SET h.departmentCodes = null")
    void clearAllDepartmentCodes();
}
