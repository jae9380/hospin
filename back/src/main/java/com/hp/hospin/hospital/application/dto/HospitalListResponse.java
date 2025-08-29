package com.hp.hospin.hospital.application.dto;

import com.hp.hospin.hospital.infrastructure.entity.Hospital;

public record HospitalListResponse(
        String hospital_code,
        String name,
        String address,
        String latitude,
        String longitude
) {
    public static HospitalListResponse from(Hospital entity) {
        return new HospitalListResponse(
                entity.getHospitalCode(),
                entity.getName(),
                entity.getAddress(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}
