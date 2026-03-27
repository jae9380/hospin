package com.hp.hospin.hospital.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HospitalBaseDTO {
    private String hospitalCode;
    private String name;
    private String address;
    private String callNumber;
    private Double latitude;
    private Double longitude;
}
