package com.hp.hospin.hospital.application;

import com.hp.hospin.global.exception.HospinCustomException.*;
import com.hp.hospin.hospital.application.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.application.dto.HospitalSearchRequest;
import com.hp.hospin.hospital.application.mapper.HospitalDtoMapper;
import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.domain.port.HospitalDetailRepository;
import com.hp.hospin.hospital.domain.port.HospitalGradeRepository;
import com.hp.hospin.hospital.domain.port.HospitalRepository;
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
    private final HospitalRepository hospitalRepository;
    private final HospitalDetailRepository hospitalDetailRepository;
    private final HospitalGradeRepository hospitalGradeRepository;
    private final HospitalDtoMapper mapper;

    public List<HospitalListResponse> getAllHospitalData() {
        return hospitalRepository.getAllData().stream()
                .map(mapper::hospitalToListResponse)
                .toList();
    }

    public HospitalInfoResponse assembleHospitalInfo(String hospitalCode) {
        Hospital hospital = hospitalRepository.findByHospitalCode(hospitalCode)
                .orElseThrow(HospitalNotExist::new);
        HospitalDetail hospitalDetail = hospitalDetailRepository.findByHospitalCode(hospitalCode)
                .orElse(null);
        HospitalGrade hospitalGrade = hospitalGradeRepository.findByHospitalCode(hospitalCode)
                .orElse(null);

        return mapper.toHospitalInfoResponse(hospital, hospitalDetail, hospitalGrade);
    }

    public List<HospitalListResponse> getHospitalsNearby(String latitude_str, String longitude_str) {
        double latitude = Double.parseDouble(latitude_str);
        double longitude = Double.parseDouble(longitude_str);

        // DB에서 정수 기준으로 1차 필터링
        List<Hospital> candidates = hospitalRepository.findHospitalsByLatLngInt((int)latitude, (int)longitude);


        return hospitalDomainService.getHospitalsNearCoordinates(latitude, longitude, candidates).stream()
                .map(mapper::hospitalToListResponse)
                .toList();
    }

    public Page<HospitalListResponse> search(String name, Long categoryCode, Long regionCode, Long districtCode,
                                             Long postalCode, String address, Pageable pageable) {
    // NOTE: 현재 간단 검색 기능만을 제공한다. 만약 상세 검색 조건을 만들 경우 해당 로직은 Domain 계층에서 작성
        HospitalSearchRequest req = new HospitalSearchRequest(
                name, categoryCode, regionCode, districtCode, postalCode, address
        );

        PageResult<Hospital> result = hospitalRepository.search(req.toDomainQuery(), pageable.getPageNumber(), pageable.getPageSize());

        var content = result.content().stream()
                .map(mapper::hospitalToListResponse)
                .toList();

        return new PageImpl<>(
                content, pageable, result.totalElements()
        );
    }
}
