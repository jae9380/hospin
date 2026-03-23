package com.hp.hospin.hospital.domain;

import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.domain.port.HospitalDetailRepository;
import com.hp.hospin.hospital.domain.port.HospitalGradeRepository;
import com.hp.hospin.hospital.domain.port.HospitalRepository;
import com.hp.hospin.hospital.domain.type.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HospitalDomainServiceImpl implements HospitalDomainService {
    private final HospitalRepository hospitalRepository;
    private final HospitalDetailRepository hospitalDetailRepository;
    private final HospitalGradeRepository hospitalGradeRepository;
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
        Double latitude  = Double.parseDouble(latitude_str);
        Double longitude = Double.parseDouble(longitude_str);

        double latRange = 3 / 111.0;
        double lngRange = 3 / (111.0 * Math.cos(Math.toRadians(latitude)));

        // DB에서 거리 계산 + 필터링 + 정렬 + (추가 옵션 LIMIT 100) 처리 완료
        return hospitalRepository.findHospitalsNearby(
                latitude - latRange, latitude + latRange,
                longitude - lngRange, longitude + lngRange,
                latitude, longitude
        );
    }

    @Override
    public List<Hospital> getTop6HospitalsNearby(String latitudeStr, String longitudeStr) {
        Double latitude  = Double.parseDouble(latitudeStr);
        Double longitude = Double.parseDouble(longitudeStr);

        double latRange = 3 / 111.0;
        double lngRange = 3 / (111.0 * Math.cos(Math.toRadians(latitude)));

        // DB에서 이미 거리순 정렬된 결과이므로 재정렬 없이 상위 6개만 추출
        return hospitalRepository.findHospitalsNearby(
                latitude - latRange, latitude + latRange,
                longitude - lngRange, longitude + lngRange,
                latitude, longitude
        ).stream()
                .limit(6)
                .toList();
    }
    @Override
    public List<List<Hospital>> findHospitalsBySymptoms(
            List<String> deptList, Double latitude, Double longitude
    ) {
        double latRange = 3 / 111.0;
        double lngRange = 3 / (111.0 * Math.cos(Math.toRadians(latitude)));

        // 1️⃣ DB에서 거리 계산 + 3km 필터링 + 거리순 정렬된 후보군 조회
        List<Hospital> candidates = hospitalRepository.findHospitalsNearby(
                latitude - latRange, latitude + latRange,
                longitude - lngRange, longitude + lngRange,
                latitude, longitude
        );

        // 2️⃣ 추천 진료과별 필터링 (candidates는 이미 3km 이내, 거리순 정렬)
        return deptList.stream()
                .limit(3) // 추천 진료과 최대 3개
                .map(DeptCode::match)
                .map(deptCode -> candidates.stream()
                        .filter(hospital -> {
                            Optional<HospitalDetail> detailOpt = hospitalDetailRepository
                                    .findByHospitalCode(hospital.getHospitalCode());
                            return detailOpt
                                    .map(detail -> detail.getDepartmentCodes().contains(deptCode.getCode()))
                                    .orElse(false);
                        })
                        .limit(3) // 각 진료과 최대 3개 (거리 재계산/재정렬 불필요)
                        .toList()
                )
                .filter(list -> !list.isEmpty()) // 병원 없는 진료과 제거
                .toList();
    }

}
