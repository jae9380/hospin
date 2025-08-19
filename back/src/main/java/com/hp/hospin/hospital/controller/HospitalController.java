package com.hp.hospin.hospital.controller;

import com.hp.hospin.hospital.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.dto.HospitalListResponse;
import com.hp.hospin.hospital.dto.HospitalSearchRequest;
import com.hp.hospin.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/search")
    public Page<HospitalListResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryCode,
            @RequestParam(required = false) Long regionCode,
            @RequestParam(required = false) Long districtCode,
            @RequestParam(required = false) Long postalCode,
            @RequestParam(required = false) String address,
            Pageable pageable
    ) {
        HospitalSearchRequest req = new HospitalSearchRequest(
                name, categoryCode, regionCode, districtCode, postalCode, address
        );
        return hospitalService.search(req, pageable);
    }
}
