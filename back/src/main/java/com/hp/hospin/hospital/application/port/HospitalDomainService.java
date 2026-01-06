package com.hp.hospin.hospital.application.port;

import com.hp.hospin.hospital.application.dto.HospitalInfoResponse.*;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.application.dto.HospitalSearchRequest;
import com.hp.hospin.hospital.application.dto.SymptomAnalyzeResponse;
import com.hp.hospin.hospital.domain.type.*;

import java.util.List;
import java.util.Optional;

public interface HospitalDomainService {
    List<Hospital> getAllHospitalData();
    Optional<Hospital> getHospitalByHospitalCode(String hospitalCode);
    Optional<HospitalDetail> getHospitalDetailByHospitalCode(String hospitalCode);
    Optional<HospitalGrade> getHospitalGradeByHospitalCode(String hospitalCode);
    PageResult<Hospital> search(HospitalSearchCriteria query, int page, int size);
    List<Hospital> getHospitalsNearCoordinates(String latitude, String longitude);
    String findHospitalsBySymptoms(SymptomAnalyzeResponse response, String latitude, String longitude);
}
