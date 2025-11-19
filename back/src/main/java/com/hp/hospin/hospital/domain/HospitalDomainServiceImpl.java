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
    public List<Hospital> getHospitalsNearCoordinates(double latitude, double longitude, List<Hospital> hospitalList) {

        // 2차 필터링: 거리 계산 후 필터
        return hospitalList.stream()
                .filter(hospital -> {
                    if (hospital.getLatitude() == null || hospital.getLongitude() == null) return false;

                    double hospLat = Double.parseDouble(hospital.getLatitude());
                    double hospLng = Double.parseDouble(hospital.getLongitude());

                    double distance = calculateDistance(latitude, longitude, hospLat, hospLng);
                    return distance <= 3;
                })
                .toList();
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
