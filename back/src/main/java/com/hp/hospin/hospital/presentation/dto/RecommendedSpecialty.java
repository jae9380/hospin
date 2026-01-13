package com.hp.hospin.hospital.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hp.hospin.hospital.application.dto.HospitalBaseDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecommendedSpecialty(
        String name,
        List<String> reasons,
        @JsonProperty("hospitals") List<HospitalBaseDTO> hospitals // null 허용
) {}