package com.hp.hospin.hospital.infrastructure.repository;

import com.hp.hospin.hospital.domain.port.HospitalRepository;
import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalSearchCriteria;
import com.hp.hospin.hospital.domain.type.PageResult;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import com.hp.hospin.hospital.infrastructure.mapper.HospitalPersistenceMapper;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalJPARepository;
import com.hp.hospin.hospital.infrastructure.repository.type.HospitalSpecificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HospitalRepositoryImpl implements HospitalRepository {
    private final HospitalJPARepository hospitalJPARepository;
    private final HospitalPersistenceMapper mapper;

    @Override
    public List<Hospital> getAllData() {
        return hospitalJPARepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Hospital> findByHospitalCode(String hospitalCode) {
        return hospitalJPARepository.findByHospitalCode(hospitalCode)
                .map(mapper::toDomain);
    }

    @Override
    public List<Hospital> findHospitalsByBoundingBox(Double minLat,  Double maxLat, Double minLng, Double maxLng) {
        return hospitalJPARepository.findHospitalsByBoundingBox(minLat, maxLat, minLng, maxLng)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PageResult<Hospital> search(HospitalSearchCriteria query, int page, int size) {
        var spec = HospitalSpecificationFactory.from(query);
        var pageable = PageRequest.of(page, size); // 정렬 필요시 여기서만 처리
        var result = hospitalJPARepository.findAll(spec, pageable);

        var content = result.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(content, page, size, result.getTotalElements());
    }

//    @Override
//    public Page<JpaHospitalEntity> search(Specification<JpaHospitalEntity> spec, Pageable pageable) {
//
//        return hospitalJPARepository.findAll(spec, pageable);
//    }
}
