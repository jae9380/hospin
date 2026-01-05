package com.hp.hospin.hospital.application.port;

import com.hp.hospin.hospital.application.dto.HospitalInfoResponse.*;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.application.dto.SymptomAnalyzeResponse;
import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalSearchCriteria;
import com.hp.hospin.hospital.domain.type.PageResult;

import java.util.List;

public interface HospitalDomainService {
    List<Hospital> getHospitalsNearCoordinates(Double latitude, Double longitude, List<Hospital> hospitalList);
    String findHospitalsBySymptoms(SymptomAnalyzeResponse response, String latitude, String longitude);
}
