package com.hp.hospin.hospital.dto;

import com.hp.hospin.hospital.entity.Hospital;

public record HospitalListResponse(
        String hospital_code,
        String name,
        String address
) {
    public static HospitalListResponse from(Hospital entity) {
        return new HospitalListResponse(
          entity.getHospitalCode(),
          entity.getName(),
          entity.getAddress()
        );
    }
}
