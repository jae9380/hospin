package com.hp.hospin.hospital.controller;

import com.hp.hospin.hospital.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.dto.HospitalListResponse;
import com.hp.hospin.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;

    @GetMapping
    public List<HospitalListResponse> getAllHospital() {
        return hospitalService.getAllHospitalData();
    }

    @GetMapping("/{hospitalCode}")
    public HospitalInfoResponse getHospitalDetails(@PathVariable(name = "hospitalCode") String hospitalCode) {
        return hospitalService.assembleHospitalInfo(hospitalCode);
    }

    @GetMapping("/nearby")
    public List<HospitalListResponse> getNearbyHospitals(@RequestParam String latitude,
                                                         @RequestParam String longitude) {
        return hospitalService.getHospitalsNearby(latitude, longitude);
    }
}
