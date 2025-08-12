package com.hp.hospin.hospital.controller;

import com.hp.hospin.hospital.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.dto.HospitalListResponse;
import com.hp.hospin.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
