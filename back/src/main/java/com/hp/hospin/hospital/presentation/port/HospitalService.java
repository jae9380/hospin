package com.hp.hospin.hospital.presentation.port;

import com.hp.hospin.hospital.application.dto.HospitalBaseDTO;
import com.hp.hospin.hospital.application.dto.HospitalDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HospitalService {
    List<HospitalBaseDTO> getAllHospitalData();
    HospitalDTO assembleHospitalInfo(String hospitalCode);
    List<HospitalBaseDTO> getHospitalsNearby(String latitude_str, String longitude_str);
    Page<HospitalBaseDTO> search(String name, Long categoryCode, Long regionCode, Long districtCode, Long postalCode, String address, Pageable pageable);
}
