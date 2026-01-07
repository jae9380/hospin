package com.hp.hospin.hospital.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecommendedSpecialty(
        String name,
        List<String> reasons
) {}