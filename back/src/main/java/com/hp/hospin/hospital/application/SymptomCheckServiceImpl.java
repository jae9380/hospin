package com.hp.hospin.hospital.application;

import com.hp.hospin.hospital.application.dto.SymptomAnalyzeResponse;
import com.hp.hospin.hospital.application.parser.SymptomAnalyzeResponseParser;
import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.application.port.SymptomAnalyzePort;
import com.hp.hospin.hospital.application.port.SymptomCheckDomainService;
import com.hp.hospin.hospital.domain.HospitalDomainServiceImpl;
import com.hp.hospin.hospital.presentation.port.SymptomCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SymptomCheckServiceImpl implements SymptomCheckService {
    private final SymptomAnalyzePort symptomAnalyzePort;
    private final HospitalDomainService hospitalDomainService;
    private final SymptomAnalyzeResponseParser parser;

    @Override
    public String generate(String text, String latitude, String longitude) {
        SymptomAnalyzeResponse result = parser.parse(symptomAnalyzePort.analyze(text));
        System.out.println(result);
        hospitalDomainService.findHospitalsBySymptoms(result, latitude, longitude);

        return "";
    }
}
