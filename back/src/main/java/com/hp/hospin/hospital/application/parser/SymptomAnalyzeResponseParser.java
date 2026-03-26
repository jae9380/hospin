package com.hp.hospin.hospital.application.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hospin.global.exception.HospinCustomException;
import com.hp.hospin.hospital.presentation.dto.SymptomAnalyzeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SymptomAnalyzeResponseParser {
    private final ObjectMapper objectMapper;

    /**
     * AI 응답(JSON 문자열)을 SymptomAnalyzeResponse로 파싱
     */
    public SymptomAnalyzeResponse parse(String json) {
        if (json == null || json.isBlank()) {
            log.error("[SymptomAnalyzeResponseParser] AI 응답이 비어있습니다.");
            throw new HospinCustomException.AiResponseParseFailed();
        }

        try {
            return objectMapper.readValue(json, SymptomAnalyzeResponse.class);
        } catch (JsonProcessingException e) {
            log.error("[SymptomAnalyzeResponseParser] AI 응답 파싱 실패: {}", e.getMessage());
            throw new HospinCustomException.AiResponseParseFailed();
        }
    }
}
