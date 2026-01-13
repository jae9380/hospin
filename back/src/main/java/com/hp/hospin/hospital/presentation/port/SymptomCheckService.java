package com.hp.hospin.hospital.presentation.port;

import com.hp.hospin.hospital.presentation.dto.SymptomAnalyzeResponse;

public interface SymptomCheckService {
    SymptomAnalyzeResponse generate(String text, Double latitude, Double longitude);
}
