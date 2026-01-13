package com.hp.hospin.hospital.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class HospitalGradeDTO {
    Map<String, Long> grades;

    public HospitalGradeDTO(Map<String, Long> grades) {
        this.grades = grades;
    }
}
