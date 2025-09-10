package com.hp.hospin.hospital.domain.type;

public record HospitalSearchCriteria(
        String name,
        Long categoryCode,
        Long regionCode,
        Long districtCode,
        Long postalCode,
        String address
) {
    public boolean isEmpty() {
        return (name == null || name.isBlank())
                && categoryCode == null && regionCode == null
                && districtCode == null && postalCode == null
                && (address == null || address.isBlank());
    }
}