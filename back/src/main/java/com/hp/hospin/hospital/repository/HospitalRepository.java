package com.hp.hospin.hospital.repository;

import com.hp.hospin.hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, String> {
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