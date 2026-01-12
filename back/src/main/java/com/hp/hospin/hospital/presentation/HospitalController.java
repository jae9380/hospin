package com.hp.hospin.hospital.presentation;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.hospital.presentation.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.presentation.dto.HospitalListResponse;
import com.hp.hospin.hospital.presentation.mapper.HospitalApiMapper;
import com.hp.hospin.hospital.presentation.port.HospitalService;
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
    private final HospitalApiMapper mapper;

    @GetMapping
    public ApiResponse<List<HospitalListResponse>> getAllHospital() {
        return ApiResponse.ok(
                hospitalService.getAllHospitalData().stream()
                .map(mapper::toListResponse)
                .toList()
        );
    }

    @GetMapping("/{hospitalCode}")
    public ApiResponse<HospitalInfoResponse> getHospitalDetails(@PathVariable(name = "hospitalCode") String hospitalCode) {
        return ApiResponse.ok(mapper.toInfoResponse(hospitalService.assembleHospitalInfo(hospitalCode)));
    }

    @GetMapping("/nearby")
    public ApiResponse<List<HospitalListResponse>> getNearbyHospitals(@RequestParam String latitude,
                                                         @RequestParam String longitude) {
        return ApiResponse.ok(
                hospitalService.getHospitalsNearby(latitude, longitude).stream()
                        .map(mapper::toListResponse)
                        .toList()
        );
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
        // TODO: 내부적으로 수정을 해야 함
        return ApiResponse.ok(
                hospitalService.search(name, categoryCode, regionCode, districtCode, postalCode, address, pageable)
                        .map(mapper::toListResponse)
        );
    }
}
