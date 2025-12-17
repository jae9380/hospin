package com.hp.hospin.hospital.batch.dto;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;

public record HospitalRegister(
        String hospitalCode,
        String name,
        String categoryCode,
        String regionCode,
        String districtCode,
        String postalCode,
        String address,
        String callNumber,
        Double latitude,
        Double longitude
) {
    public JpaHospitalEntity to() {
        return JpaHospitalEntity.create(
                hospitalCode,
                name,
                categoryCode,
                regionCode,
                districtCode,
                postalCode,
                address,
                callNumber,
                latitude,
                longitude
        );
    }
}
