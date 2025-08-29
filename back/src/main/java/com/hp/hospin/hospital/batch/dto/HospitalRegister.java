package com.hp.hospin.hospital.batch.dto;

import com.hp.hospin.hospital.infrastructure.entity.Hospital;

public record HospitalRegister(
        String hospitalCode,
        String name,
        String categoryCode,
        String regionCode,
        String districtCode,
        String postalCode,
        String address,
        String callNumber,
        String latitude,
        String longitude
) {
    public Hospital to() {
        return Hospital.create(
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
