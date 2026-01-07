package com.hp.hospin.hospital.application;

import com.hp.hospin.hospital.presentation.dto.SymptomAnalyzeResponse;
import com.hp.hospin.hospital.application.parser.SymptomAnalyzeResponseParser;
import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.application.port.SymptomAnalyzePort;
import com.hp.hospin.hospital.presentation.port.SymptomCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
