package com.hp.hospin.hospital.presentation.port;

import com.hp.hospin.hospital.application.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HospitalService {
    List<HospitalListResponse> getAllHospitalData();
    HospitalInfoResponse assembleHospitalInfo(String hospitalCode);
    List<HospitalListResponse> getHospitalsNearby(String latitude_str, String longitude_str);
    Page<HospitalListResponse> search(String name, Long categoryCode, Long regionCode, Long districtCode, Long postalCode, String address, Pageable pageable);
}
