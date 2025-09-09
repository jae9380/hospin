package com.hp.hospin.hospital.application.port;

import com.hp.hospin.hospital.application.dto.HospitalInfoResponse.*;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalSearchCriteria;
import com.hp.hospin.hospital.domain.type.PageResult;

import java.util.List;

public interface HospitalDomainService {
    List<HospitalListResponse> getAllHospitalData();
    BaseInfo getHospitalAsBaseInfoByCode(String hospitalCode);
    DetailInfo getHospitalDetailAsDetailInfoByCode(String hospitalCode);
    GradeInfo getHospitalGradeAsGradeInfoByCode(String hospitalCode);
    List<HospitalListResponse> getHospitalsNearCoordinates(String latitude_str, String longitude_str);
//    Page<HospitalListResponse> search(HospitalSearchRequest req, Pageable pageable);
    PageResult<Hospital> search(HospitalSearchCriteria query, int page, int size);
}
