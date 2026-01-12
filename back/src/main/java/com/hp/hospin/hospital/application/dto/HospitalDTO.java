package com.hp.hospin.hospital.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HospitalDTO {
    HospitalBaseDTO base;
    HospitalDetailDTO detail;
    HospitalGradeDTO grade;
}
