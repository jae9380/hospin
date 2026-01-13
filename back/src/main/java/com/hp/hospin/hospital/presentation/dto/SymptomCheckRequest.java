package com.hp.hospin.hospital.presentation.dto;

public record SymptomCheckRequest(
        String str,
        Double latitude,
        Double longitude
) {}
