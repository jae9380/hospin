package com.hp.hospin.hospital.domain;

import com.hp.hospin.global.exception.HospinCustomException.*;
import com.hp.hospin.hospital.application.dto.HospitalInfoResponse.*;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.application.dto.HospitalSearchRequest;
import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.domain.port.HospitalDetailRepository;
import com.hp.hospin.hospital.domain.port.HospitalGradeRepository;
import com.hp.hospin.hospital.domain.port.HospitalRepository;
import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalSearchCriteria;
import com.hp.hospin.hospital.domain.type.PageResult;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;
import com.hp.hospin.hospital.infrastructure.repository.HospitalSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HospitalDomainServiceImpl implements HospitalDomainService {
    private final HospitalRepository hospitalRepository;
    private final HospitalDetailRepository hospitalDetailRepository;
    private final HospitalGradeRepository hospitalGradeRepository;

    private final static int R = 6371;

    @Override
    public List<HospitalListResponse> getAllHospitalData() {
        return hospitalRepository.getAllData().stream()
                .map(HospitalListResponse::from)
                .toList();
    }

    @Override
    public BaseInfo getHospitalAsBaseInfoByCode(String hospitalCode) {
        try {
            Hospital hospital = findHospitalByCode(hospitalCode);

            return BaseInfo.builder()
                    .hospitalCode(hospital.getHospitalCode())
                    .name(hospital.getName())
                    .address(hospital.getAddress())
                    .callNumber(hospital.getCallNumber())
                    .latitude(hospital.getLatitude())
                    .longitude(hospital.getLongitude())
                    .build();
        } catch (HospitalNotExist e) {
            return null;
        }
    }

    @Override
    public DetailInfo getHospitalDetailAsDetailInfoByCode(String hospitalCode) {
        try {
            JpaHospitalDetailEntity detail = findHospitalDetailByCode(hospitalCode);

            return DetailInfo.builder()
                    .departmentCodes(detail.getDepartmentCodes())
                    .closedSunday(detail.getClosedSunday())
                    .closedHoliday(detail.getClosedHoliday())
                    .emergencyDayYn(detail.getEmergencyDayYn())
                    .emergencyDayPhone1(detail.getEmergencyDayPhone1())
                    .emergencyDayPhone2(detail.getEmergencyDayPhone2())
                    .emergencyNightYn(detail.getEmergencyNightYn())
                    .emergencyNightPhone1(detail.getEmergencyNightPhone1())
                    .emergencyNightPhone2(detail.getEmergencyNightPhone2())
                    .lunchWeekday(detail.getLunchWeekday())
                    .lunchSaturday(detail.getLunchSaturday())
                    .receptionWeekday(detail.getReceptionWeekday())
                    .receptionSaturday(detail.getReceptionSaturday())
                    .treatSunStart(detail.getTreatSunStart())
                    .treatSunEnd(detail.getTreatSunEnd())
                    .treatMonStart(detail.getTreatMonStart())
                    .treatMonEnd(detail.getTreatMonEnd())
                    .treatTueStart(detail.getTreatTueStart())
                    .treatTueEnd(detail.getTreatTueEnd())
                    .treatWedStart(detail.getTreatWedStart())
                    .treatWedEnd(detail.getTreatWedEnd())
                    .treatThuStart(detail.getTreatThuStart())
                    .treatThuEnd(detail.getTreatThuEnd())
                    .treatFriStart(detail.getTreatFriStart())
                    .treatFriEnd(detail.getTreatFriEnd())
                    .treatSatStart(detail.getTreatSatStart())
                    .treatSatEnd(detail.getTreatSatEnd())
                    .build();
        } catch (HospitalNotExist e) {
            return null;
        }
    }

    @Override
    public GradeInfo getHospitalGradeAsGradeInfoByCode(String hospitalCode) {
        try {
            JpaHospitalGradeEntity grade = findHospitalGradeByCode(hospitalCode);

            return GradeInfo.builder()
                    .grades(extractGrades(grade))
                    .build();
        } catch (HospitalNotExist e) {
            return null;
        }
    }

    @Override
    public List<HospitalListResponse> getHospitalsNearCoordinates(String latitude_str, String longitude_str) {
        double latitude = Double.parseDouble(latitude_str);
        double longitude = Double.parseDouble(longitude_str);

        // DB에서 정수 기준으로 1차 필터링
        List<Hospital> candidates = hospitalRepository.findHospitalsByLatLngInt((int)latitude, (int)longitude);

        // 2차 필터링: 거리 계산 후 필터
        return candidates.stream()
                .filter(hospital -> {
                    if (hospital.getLatitude() == null || hospital.getLongitude() == null) return false;

                    double hospLat = Double.parseDouble(hospital.getLatitude());
                    double hospLng = Double.parseDouble(hospital.getLongitude());

                    double distance = calculateDistance(latitude, longitude, hospLat, hospLng);
                    return distance <= 3;
                })
                .map(hospital -> new HospitalListResponse(
                        hospital.getHospitalCode(),
                        hospital.getName(),
                        hospital.getAddress(),
                        String.valueOf(hospital.getLatitude()),
                        String.valueOf(hospital.getLongitude())
                ))
                .toList();
    }

    // TODO: 내부적으로 수정을 해야 함
//    @Override
//    public Page<HospitalListResponse> search(HospitalSearchRequest req, Pageable pageable) {
//        Specification<JpaHospitalEntity> spec = Specification.where(HospitalSpecs.nameContains(req.name()))
//                .and(HospitalSpecs.categoryEq(req.categoryCode()))
//                .and(HospitalSpecs.regionEq(req.regionCode()))
//                .and(HospitalSpecs.districtEq(req.districtCode()))
//                .and(HospitalSpecs.postalEq(req.postalCode()))
//                .and(HospitalSpecs.addressContains(req.address()));
//
//        return hospitalRepository.search(spec, pageable)
//                .map(HospitalListResponse::from);
//    }

    public PageResult<Hospital> search(HospitalSearchCriteria query, int page, int size) {
        return hospitalRepository.search(query, page, size);
    }
//

    //    Haversine 공식
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private Map<String, Long> extractGrades(JpaHospitalGradeEntity grade) {
        Map<String, Long> grades = new LinkedHashMap<>();

        for (int i = 1; i <= 24; i++) {
            String key = String.format("asmGrd%02d", i);
            String value = getGradeValue(grade, key);
            grades.put(key, convertGrade(value));
        }
        return grades;
    }

    private String getGradeValue(JpaHospitalGradeEntity grade, String fieldName) {
        try {
            Field field = JpaHospitalGradeEntity.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (String) field.get(grade);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Invalid grade field: " + fieldName, e);
        }
    }

    private Long convertGrade(String gradeStr) {
        if (gradeStr == null) return null;
        if (gradeStr.equals("등급 제외")) return 0L;

        try {
            return Long.parseLong(gradeStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid grade string: " + gradeStr);
        }
    }

    private Hospital findHospitalByCode(String hospitalCode) {
        return hospitalRepository.findByHospitalCode(hospitalCode)
                .orElseThrow(HospitalNotExist::new);
    }

    private JpaHospitalDetailEntity findHospitalDetailByCode(String hospitalCode) {
        return hospitalDetailRepository.findByHospitalCode(hospitalCode)
                .orElseThrow(HospitalNotExist::new);
    }

    private JpaHospitalGradeEntity findHospitalGradeByCode(String hospitalCode) {
        return hospitalGradeRepository.findByHospitalCode(hospitalCode)
                .orElseThrow(HospitalNotExist::new);
    }

}
