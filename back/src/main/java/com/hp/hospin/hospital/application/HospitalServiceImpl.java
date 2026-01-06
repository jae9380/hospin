package com.hp.hospin.hospital.application;

import com.hp.hospin.global.exception.HospinCustomException.*;
import com.hp.hospin.hospital.application.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.application.dto.HospitalSearchRequest;
import com.hp.hospin.hospital.application.mapper.HospitalDtoMapper;
import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalDetail;
import com.hp.hospin.hospital.domain.type.HospitalGrade;
import com.hp.hospin.hospital.domain.type.PageResult;
import com.hp.hospin.hospital.presentation.port.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalServiceImpl implements HospitalService {
    private final HospitalDomainService hospitalDomainService;
    private final HospitalDtoMapper mapper;

    public List<HospitalListResponse> getAllHospitalData() {
        List<Hospital> hospitals = hospitalDomainService.getAllHospitalData();

        return hospitals.stream()
                .map(mapper::hospitalToListResponse)
                .toList();
    }

    public HospitalInfoResponse assembleHospitalInfo(String hospitalCode) {
        Hospital hospital = hospitalDomainService.getHospitalByHospitalCode(hospitalCode)
                .orElseThrow(HospitalNotExist::new);
        HospitalDetail hospitalDetail = hospitalDomainService.getHospitalDetailByHospitalCode(hospitalCode)
                .orElse(null);
        HospitalGrade hospitalGrade = hospitalDomainService.getHospitalGradeByHospitalCode(hospitalCode)
                .orElse(null);

        return mapper.toHospitalInfoResponse(hospital, hospitalDetail, hospitalGrade);
    }

    public List<HospitalListResponse> getHospitalsNearby(String latitude_str, String longitude_str) {
        List<HospitalListResponse> result = hospitalDomainService.getHospitalsNearCoordinates(latitude_str, longitude_str).stream()
                .map(mapper::hospitalToListResponse)
                .toList();

        return result;
    }

@Override
public Page<HospitalListResponse> search(String name, Long categoryCode, Long regionCode,
                                            Long districtCode, Long postalCode, String address, Pageable pageable) {
    HospitalSearchRequest req = new HospitalSearchRequest(
            name, categoryCode, regionCode, districtCode, postalCode, address
    );

    PageResult<Hospital> result = hospitalDomainService.search(
            req.toDomainQuery(),
            pageable.getPageNumber(),
            pageable.getPageSize()
    );

    var content = result.content().stream()
            .map(mapper::hospitalToListResponse)
            .toList();

    return new PageImpl<>(content, pageable, result.totalElements());
}
}
