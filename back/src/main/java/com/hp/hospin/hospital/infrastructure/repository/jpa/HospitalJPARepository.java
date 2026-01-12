package com.hp.hospin.hospital.infrastructure.repository.jpa;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HospitalJPARepository extends JpaRepository<JpaHospitalEntity, String>, JpaSpecificationExecutor<JpaHospitalEntity> {
    Optional<JpaHospitalEntity> findByHospitalCode(String hospitalCode);

    @Query(value = "SELECT * FROM hospitals h " +
            "WHERE h.latitude BETWEEN :minLat AND :maxLat " +
            "AND h.longitude BETWEEN :minLng AND :maxLng",
            nativeQuery = true)
    List<JpaHospitalEntity> findHospitalsByBoundingBox(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng
    );

    @Query(value = """
    SELECT DISTINCT h.*
    FROM hospitals h
    JOIN hospital_detail d ON h.hospital_code = d.hospital_code
    JOIN hospital_department hd ON d.hospital_code = hd.hospital_code
    WHERE h.latitude BETWEEN :minLat AND :maxLat
      AND h.longitude BETWEEN :minLng AND :maxLng
      AND hd.department_code IN (:deptCodes)
""", nativeQuery = true)
    List<JpaHospitalEntity> findHospitalsByLocationAndDeptCodes(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng,
            @Param("deptCodes") List<String> deptCodes
    );
}