package com.hp.hospin.hospital.repository;

import com.hp.hospin.hospital.entity.Hospital;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

@NoArgsConstructor
public final class HospitalSpecs {
    public static Specification<Hospital> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            String pattern = "%" + name.toLowerCase(Locale.ROOT).trim() + "%";
            return cb.like(cb.lower(root.get("name")), pattern);
        };
    }

    public static Specification<Hospital> addressContains(String address) {
        return (root, query, cb) -> {
            if (address == null || address.isBlank()) return null;
            String pattern = "%" + address.toLowerCase(Locale.ROOT).trim() + "%";
            return cb.like(cb.lower(root.get("address")), pattern);
        };
    }

    public static Specification<Hospital> categoryEq(Long code) {
        return (root, query, cb) -> code == null ? null : cb.equal(root.get("categoryCode"), code);
    }

    public static Specification<Hospital> regionEq(Long code) {
        return (root, query, cb) -> code == null ? null : cb.equal(root.get("regionCode"), code);
    }

    public static Specification<Hospital> districtEq(Long code) {
        return (root, query, cb) -> code == null ? null : cb.equal(root.get("districtCode"), code);
    }

    public static Specification<Hospital> postalEq(Long code) {
        return (root, query, cb) -> code == null ? null : cb.equal(root.get("postalCode"), code);
    }
}