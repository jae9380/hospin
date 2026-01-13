package com.hp.hospin.hospital.application.dto;

import com.hp.hospin.hospital.domain.type.HospitalSearchCriteria;
import io.micrometer.common.lang.Nullable;

public record HospitalSearchRequest(
        @Nullable
        String name,
        @Nullable
        Long categoryCode,
        @Nullable
        Long regionCode,
        @Nullable
        Long districtCode,
        @Nullable
        Long postalCode,
        @Nullable
        String address
) {
    public HospitalSearchCriteria toDomainQuery() {
        return new HospitalSearchCriteria(name, categoryCode, regionCode, districtCode, postalCode, address);
    }
}