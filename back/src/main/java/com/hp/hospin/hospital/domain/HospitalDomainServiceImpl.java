package com.hp.hospin.hospital.domain;

import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.domain.type.Hospital;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalDomainServiceImpl implements HospitalDomainService {
    private final static int R = 6371;

    @Override
    public List<Hospital> getHospitalsNearCoordinates(Double latitude, Double longitude, List<Hospital> hospitalList) {

        // 2차 필터링: 거리 계산 후 필터
        return hospitalList.stream()
                .filter(hospital -> {
                    if (hospital.getLatitude() == null || hospital.getLongitude() == null) return false;

                    Double hospLat = hospital.getLatitude();
                    Double hospLng = hospital.getLongitude();

                    Double distance = calculateDistance(latitude, longitude, hospLat, hospLng);
                    return distance <= 3;
                })
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
