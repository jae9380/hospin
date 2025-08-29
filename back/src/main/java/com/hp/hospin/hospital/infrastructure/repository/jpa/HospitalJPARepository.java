package com.hp.hospin.hospital.infrastructure.repository.jpa;

import com.hp.hospin.hospital.infrastructure.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HospitalJPARepository extends JpaRepository<Hospital, String>, JpaSpecificationExecutor<Hospital> {
    Optional<Hospital> findByHospitalCode(String hospitalCode);

    @Query(value = "SELECT * FROM hospitals h " +
            "WHERE h.latitude <> '' AND h.longitude <> '' " +
            "AND FLOOR(CAST(h.latitude AS numeric)) = :latInt " +
            "AND FLOOR(CAST(h.longitude AS numeric)) = :lngInt", nativeQuery = true)
    List<Hospital> findHospitalsByLatLngInt(
            @Param("latInt") int latInt,
            @Param("lngInt") int lngInt
    );
}