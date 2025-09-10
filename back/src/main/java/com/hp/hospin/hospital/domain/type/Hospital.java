package com.hp.hospin.hospital.domain.type;

import lombok.Getter;

@Getter
public class Hospital {
    private final String hospitalCode;
    private final String name;
    private final Long categoryCode;
    private final Long regionCode;
    private final Long districtCode;
    private final Long postalCode;
    private final String address;
    private final String callNumber;
    private final String latitude;
    private final String longitude;

    public Hospital(String hospitalCode,
                    String name,
                    Long categoryCode,
                    Long regionCode,
                    Long districtCode,
                    Long postalCode,
                    String address,
                    String callNumber,
                    String latitude,
                    String longitude) {
        this.hospitalCode = hospitalCode;
        this.name = name;
        this.categoryCode = categoryCode;
        this.regionCode = regionCode;
        this.districtCode = districtCode;
        this.postalCode = postalCode;
        this.address = address;
        this.callNumber = callNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
