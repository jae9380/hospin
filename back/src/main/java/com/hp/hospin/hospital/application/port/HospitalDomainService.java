package com.hp.hospin.hospital.application.port;

import com.hp.hospin.hospital.application.dto.HospitalInfoResponse.*;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalSearchCriteria;
import com.hp.hospin.hospital.domain.type.PageResult;

import java.util.List;

public interface HospitalDomainService {
    List<Hospital> getHospitalsNearCoordinates(double latitude, double longitude, List<Hospital> hospitalList);
}
