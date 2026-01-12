package com.hp.hospin.hospital.presentation.dto;

import com.hp.hospin.hospital.domain.type.Hospital;

public record HospitalListResponse(
        String hospital_code,
        String name,
        String address,
        Double latitude,
        Double longitude
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
