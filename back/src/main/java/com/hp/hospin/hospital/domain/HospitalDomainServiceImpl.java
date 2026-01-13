package com.hp.hospin.hospital.domain;

import com.hp.hospin.hospital.presentation.dto.SymptomAnalyzeResponse;
import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.domain.port.HospitalDetailRepository;
import com.hp.hospin.hospital.domain.port.HospitalGradeRepository;
import com.hp.hospin.hospital.domain.port.HospitalRepository;
import com.hp.hospin.hospital.domain.type.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HospitalDomainServiceImpl implements HospitalDomainService {
    private final HospitalRepository hospitalRepository;
    private final HospitalDetailRepository hospitalDetailRepository;
    private final HospitalGradeRepository hospitalGradeRepository;
    private final static int R = 6371;


    @Override
    public List<Hospital> getAllHospitalData() {
        return hospitalRepository.getAllData();
    }

    @Override
    public Optional<Hospital> getHospitalByHospitalCode(String hospitalCode) {
        return hospitalRepository.findByHospitalCode(hospitalCode);
    }


    @Override
    public Optional<HospitalDetail> getHospitalDetailByHospitalCode(String hospitalCode) {
        return hospitalDetailRepository.findByHospitalCode(hospitalCode);
    }

    @Override
    public Optional<HospitalGrade> getHospitalGradeByHospitalCode(String hospitalCode) {
        return hospitalGradeRepository.findByHospitalCode(hospitalCode);
    }

    @Override
    public PageResult<Hospital> search(HospitalSearchCriteria query, int page, int size) {
        return hospitalRepository.search(query, page, size);
    }


    @Override
    public List<Hospital> getHospitalsNearCoordinates(String latitude_str, String longitude_str) {
        Double latitude = Double.parseDouble(latitude_str);
        Double longitude = Double.parseDouble(longitude_str);

        double latRange = 3 / 111.0;
        double lngRange = 3 / (111.0 * Math.cos(Math.toRadians(latitude)));

        double minLat = latitude - latRange;
        double maxLat = latitude + latRange;
        double minLng = longitude - lngRange;
        double maxLng = longitude + lngRange;

        // note: 1차 필터링 - DB에서 정수 기준으로 필터링
        List<Hospital> candidates = hospitalRepository.findHospitalsByBoundingBox(latitude, latitude, longitude, longitude);

        // note: 2차 필터링 - 거리 계산 후 필터fld
        return candidates.stream()
                .filter(hospital -> {
                    if (hospital.getLatitude() == null || hospital.getLongitude() == null) return false;

                    Double hospLat = hospital.getLatitude();
                    Double hospLng = hospital.getLongitude();

                    Double distance = calculateDistance(latitude, longitude, hospLat, hospLng);
                    return distance <= 3;
                })
                .toList();
    }

    @Override
    public List<List<Hospital>> findHospitalsBySymptoms(
            List<String> deptList, Double latitude, Double longitude
    ) {
        double lat = latitude;
        double lng = longitude;

        double latRange = 3 / 111.0;
        double lngRange = 3 / (111.0 * Math.cos(Math.toRadians(lat)));

        // 1️⃣ 위도/경도 후보군 조회
        List<Hospital> candidates = hospitalRepository.findHospitalsByBoundingBox(
                lat - latRange, lat + latRange,
                lng - lngRange, lng + lngRange
        );

        // 2️⃣ 추천 진료과별 필터링
        return deptList.stream()
                .limit(3) // 추천 진료과 최대 3개
                .map(DeptCode::match)
                .map(deptCode -> candidates.stream()
                        .filter(hospital -> {
                            // HospitalDetail 조회
                            Optional<HospitalDetail> detailOpt = hospitalDetailRepository
                                    .findByHospitalCode(hospital.getHospitalCode());
                            return detailOpt
                                    .map(detail -> detail.getDepartmentCodes().contains(deptCode.getCode()))
                                    .orElse(false);
                        })
                        .filter(hospital -> calculateDistance(
                                lat, lng, hospital.getLatitude(), hospital.getLongitude()) <= 3
                        )
                        .sorted((a, b) -> Double.compare(
                                calculateDistance(lat, lng, a.getLatitude(), a.getLongitude()),
                                calculateDistance(lat, lng, b.getLatitude(), b.getLongitude())
                        ))
                        .limit(3) // 각 진료과 최대 3개
                        .toList()
                )
                .filter(list -> !list.isEmpty()) // 병원 없는 진료과 제거
                .toList();
    }

    //    Haversine 공식
    private Double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        Double dLat = Math.toRadians(lat2 - lat1);
        Double dLng = Math.toRadians(lng2 - lng1);

        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
