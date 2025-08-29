package com.hp.hospin.hospital.application;

import com.hp.hospin.hospital.application.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.application.dto.HospitalInfoResponse.*;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.application.dto.HospitalSearchRequest;
import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.infrastructure.entity.Hospital;
import com.hp.hospin.hospital.infrastructure.repository.HospitalSpecs;
import com.hp.hospin.hospital.presentation.port.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    public Page<HospitalListResponse> search(HospitalSearchRequest req, Pageable pageable) {
        return hospitalDomainService.search(req, pageable);
    }
}
