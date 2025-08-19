package com.hp.hospin.hospital.service;

import com.hp.hospin.hospital.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.dto.HospitalInfoResponse.BaseInfo;
import com.hp.hospin.hospital.dto.HospitalInfoResponse.DetailInfo;
import com.hp.hospin.hospital.dto.HospitalInfoResponse.GradeInfo;
import com.hp.hospin.hospital.dto.HospitalListResponse;
import com.hp.hospin.hospital.entity.Hospital;
import com.hp.hospin.hospital.entity.HospitalDetail;
import com.hp.hospin.hospital.entity.HospitalGrade;
import com.hp.hospin.hospital.repository.HospitalDetailRepository;
import com.hp.hospin.hospital.repository.HospitalGradeRepository;
import com.hp.hospin.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    private final HospitalDetailRepository hospitalDetailRepository;
    private final HospitalGradeRepository hospitalGradeRepository;

    private final static int R = 6371;

    public List<HospitalListResponse> getAllHospitalData() {
        return hospitalRepository.findAll().stream()
                .map(HospitalListResponse::from)
                .toList();
    }

    public HospitalInfoResponse assembleHospitalInfo(String hospitalCode) {
        Hospital hospital = findHospitalByCode(hospitalCode);

        BaseInfo baseInfo= BaseInfo.builder()
                .hospitalCode(hospital.getHospitalCode())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .callNumber(hospital.getCallNumber())
                .latitude(hospital.getLatitude())
                .longitude(hospital.getLongitude())
                .build();

        HospitalDetail detail = hospitalDetailRepository.findByHospitalCode(hospitalCode)
                .orElse(null);

        DetailInfo detailInfo = null;

        if (detail != null) {
            detailInfo = DetailInfo.builder()
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
        }


        HospitalGrade grade = hospitalGradeRepository.findByHospitalCode(hospitalCode)
                .orElse(null);

        GradeInfo gradeInfo = null;

        if (grade != null) {
            gradeInfo =  GradeInfo.builder()
                    .grades(extractGrades(grade))
                    .build();
        }


        return HospitalInfoResponse.builder()
                .baseInfo(baseInfo)
                .detailInfo(detailInfo)
                .gradeInfo(gradeInfo)
                .build();
    }

    public List<HospitalListResponse> getHospitalsNearby(String latitude_str, String longitude_str) {
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

    private Hospital findHospitalByCode(String hospitalCode) {
        return hospitalRepository.findByHospitalCode(hospitalCode)
                .orElseThrow(RuntimeException::new);
    }


    private Map<String, Long> extractGrades(HospitalGrade grade) {
        Map<String, Long> grades = new LinkedHashMap<>();

        for (int i = 1; i <= 24; i++) {
            String key = String.format("asmGrd%02d", i);
            String value = getGradeValue(grade, key);
            grades.put(key, convertGrade(value));
        }
        return grades;
    }

    private String getGradeValue(HospitalGrade grade, String fieldName) {
        try {
            Field field = HospitalGrade.class.getDeclaredField(fieldName);
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
}
