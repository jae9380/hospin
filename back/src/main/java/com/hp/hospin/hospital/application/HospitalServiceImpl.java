package com.hp.hospin.hospital.application;

import com.hp.hospin.hospital.application.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.application.dto.HospitalInfoResponse.*;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.application.dto.HospitalSearchRequest;
import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.domain.type.Hospital;
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
    public List<HospitalListResponse> getAllHospitalData() {
        return hospitalDomainService.getAllHospitalData();
    }

    public HospitalInfoResponse assembleHospitalInfo(String hospitalCode) {
        BaseInfo baseInfo = hospitalDomainService.getHospitalAsBaseInfoByCode(hospitalCode);
        DetailInfo detailInfo = hospitalDomainService.getHospitalDetailAsDetailInfoByCode(hospitalCode);
        GradeInfo gradeInfo = hospitalDomainService.getHospitalGradeAsGradeInfoByCode(hospitalCode);

        return HospitalInfoResponse.builder()
                .baseInfo(baseInfo)
                .detailInfo(detailInfo)
                .gradeInfo(gradeInfo)
                .build();
    }

    public List<HospitalListResponse> getHospitalsNearby(String latitude_str, String longitude_str) {
        return hospitalDomainService.getHospitalsNearCoordinates(latitude_str, longitude_str);
    }

//    // TODO: 내부적으로 수정을 해야 함
//    public Page<HospitalListResponse> search(String name, Long categoryCode, Long regionCode, Long districtCode,
//                                             Long postalCode, String address, Pageable pageable) {
//        HospitalSearchRequest req = new HospitalSearchRequest(
//                name, categoryCode, regionCode, districtCode, postalCode, address
//        );
//        return hospitalDomainService.search(req, pageable);
//    }

    public Page<HospitalListResponse> search(String name, Long categoryCode, Long regionCode, Long districtCode,
                                             Long postalCode, String address, Pageable pageable) {

        HospitalSearchRequest req = new HospitalSearchRequest(
                name, categoryCode, regionCode, districtCode, postalCode, address
        );
        PageResult<Hospital> result = hospitalDomainService.search(req.toDomainQuery(), pageable.getPageNumber(), pageable.getPageSize());

        // Domain PageResult -> Spring Page 변환
        var content = result.content().stream()
                .map(HospitalListResponse::from)
                .toList();

        return new PageImpl<>(
                content, pageable, result.totalElements()
        );
    }
}
