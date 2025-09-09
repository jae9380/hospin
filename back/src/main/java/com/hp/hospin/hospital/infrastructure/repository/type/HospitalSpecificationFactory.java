package com.hp.hospin.hospital.infrastructure.repository.type;

import com.hp.hospin.hospital.domain.type.HospitalSearchCriteria;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public final class HospitalSpecificationFactory {

    public static Specification<JpaHospitalEntity> from(HospitalSearchCriteria q) {
        Specification<JpaHospitalEntity> spec = Specification.where(null);

        if (q.name() != null && !q.name().isBlank()) {
            spec = spec.and((root, cbQuery, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + q.name().toLowerCase() + "%"));
        }
        if (q.categoryCode() != null) {
            spec = spec.and((root, cbQuery, cb) -> cb.equal(root.get("categoryCode"), q.categoryCode()));
        }
        if (q.regionCode() != null) {
            spec = spec.and((root, cbQuery, cb) -> cb.equal(root.get("regionCode"), q.regionCode()));
        }
        if (q.districtCode() != null) {
            spec = spec.and((root, cbQuery, cb) -> cb.equal(root.get("districtCode"), q.districtCode()));
        }
        if (q.postalCode() != null) {
            spec = spec.and((root, cbQuery, cb) -> cb.equal(root.get("postalCode"), q.postalCode()));
        }
        if (q.address() != null && !q.address().isBlank()) {
            spec = spec.and((root, cbQuery, cb) ->
                    cb.like(cb.lower(root.get("address")), "%" + q.address().toLowerCase() + "%"));
        }
        return spec;
    }
}
