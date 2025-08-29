package com.hp.hospin.hospital.application.dto;

import io.micrometer.common.lang.Nullable;

public record HospitalSearchRequest(
        @Nullable String name,
        @Nullable Long categoryCode,
        @Nullable Long regionCode,
        @Nullable Long districtCode,
        @Nullable Long postalCode,
        @Nullable String address
) {}