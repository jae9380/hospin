package com.hp.hospin.hospital.presentation;


import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.hospital.presentation.dto.SymptomAnalyzeResponse;
import com.hp.hospin.hospital.presentation.dto.SymptomCheckRequest;
import com.hp.hospin.hospital.presentation.port.SymptomCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/symptomcheck")
@RequiredArgsConstructor
public class SymptomCheckController {
    private final SymptomCheckService symptomCheckService;

    @PostMapping("")
    public ApiResponse<SymptomAnalyzeResponse> percheck(@RequestBody SymptomCheckRequest request) {
        return ApiResponse.ok(symptomCheckService.generate(request.str(), request.latitude(), request.longitude()));
    }
}
