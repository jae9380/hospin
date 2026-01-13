package com.hp.hospin.hospital.presentation;


import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.hospital.presentation.dto.SymptomAnalyzeResponse;
import com.hp.hospin.hospital.presentation.dto.SymptomCheckRequest;
import com.hp.hospin.hospital.presentation.port.SymptomCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/symptomcheck")
@RequiredArgsConstructor
public class SymptomCheckController {
    private final SymptomCheckService symptomCheckService;
    @PostMapping("")
    public ApiResponse<SymptomAnalyzeResponse> percheck(@RequestBody SymptomCheckRequest request) {
        try {
            System.out.println(request.toString());
            SymptomAnalyzeResponse result = symptomCheckService.generate(request.str(), request.latitude(), request.longitude()); // 호출 시 401 발생 가능
            System.out.println(result.toString());
            return ApiResponse.ok(result);
        } catch (Exception e) {
            System.out.println("API 호출 실패: " + e.getMessage());
            return ApiResponse.ok(null);
        }
    }
}
