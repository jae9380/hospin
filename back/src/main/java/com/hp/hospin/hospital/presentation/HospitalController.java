package com.hp.hospin.hospital.presentation;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.hospital.application.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.application.dto.HospitalSearchRequest;
import com.hp.hospin.hospital.presentation.port.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospital")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;

    @GetMapping
    public ApiResponse<List<HospitalListResponse>> getAllHospital() {
        return ApiResponse.ok(hospitalService.getAllHospitalData());
    }

    @GetMapping("/{hospitalCode}")
    public ApiResponse<HospitalInfoResponse> getHospitalDetails(@PathVariable(name = "hospitalCode") String hospitalCode) {
        return ApiResponse.ok(hospitalService.assembleHospitalInfo(hospitalCode));
    }

    @GetMapping("/nearby")
    public ApiResponse<List<HospitalListResponse>> getNearbyHospitals(@RequestParam String latitude,
                                                         @RequestParam String longitude) {
        return ApiResponse.ok(hospitalService.getHospitalsNearby(latitude, longitude));
    }

    @GetMapping("/search")
    public ApiResponse<Page<HospitalListResponse>> search(
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
        return ApiResponse.ok(hospitalService.search(req, pageable));
    }
}
