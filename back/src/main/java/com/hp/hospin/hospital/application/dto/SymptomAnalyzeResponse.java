package com.hp.hospin.hospital.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SymptomAnalyzeResponse(
        List<RecommendedSpecialty> recommended_specialties
) {}