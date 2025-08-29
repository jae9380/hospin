package com.hp.hospin.hospital.application.port;

import com.hp.hospin.hospital.application.dto.HospitalInfoResponse.*;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.application.dto.HospitalSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HospitalDomainService {
    List<HospitalListResponse> getAllHospitalData();
    BaseInfo getHospitalAsBaseInfoByCode(String hospitalCode);
    DetailInfo getHospitalDetailAsDetailInfoByCode(String hospitalCode);
    GradeInfo getHospitalGradeAsGradeInfoByCode(String hospitalCode);
    List<HospitalListResponse> getHospitalsNearCoordinates(String latitude_str, String longitude_str);
    Page<HospitalListResponse> search(HospitalSearchRequest req, Pageable pageable);
}
