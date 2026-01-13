package com.hp.hospin.hospital.application.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hospin.hospital.presentation.dto.SymptomAnalyzeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SymptomAnalyzeResponseParser {
    private final ObjectMapper objectMapper;

    /**
     * AI 응답(JSON 문자열)을 SymptomAnalyzeResponse로 파싱
     */
    public SymptomAnalyzeResponse parse(String json) {
        if (json == null || json.isBlank()) {
            return new SymptomAnalyzeResponse(java.util.List.of());
        }

        try {
            return objectMapper.readValue(json, SymptomAnalyzeResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "증상 분석 AI 응답 파싱 실패",
                    e
            );
        }
    }
}