package com.hp.hospin.hospital.application;

import com.hp.hospin.hospital.application.dto.HospitalBaseDTO;
import com.hp.hospin.hospital.application.mapper.HospitalDtoMapper;
import com.hp.hospin.hospital.presentation.dto.RecommendedSpecialty;
import com.hp.hospin.hospital.presentation.dto.SymptomAnalyzeResponse;
import com.hp.hospin.hospital.application.parser.SymptomAnalyzeResponseParser;
import com.hp.hospin.hospital.application.port.HospitalDomainService;
import com.hp.hospin.hospital.application.port.SymptomAnalyzePort;
import com.hp.hospin.hospital.presentation.port.SymptomCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SymptomCheckServiceImpl implements SymptomCheckService {
    private final SymptomAnalyzePort symptomAnalyzePort;
    private final HospitalDomainService hospitalDomainService;
    private final SymptomAnalyzeResponseParser parser;
    private final HospitalDtoMapper mapper;

    @Override
    public SymptomAnalyzeResponse generate(String text, Double latitude, Double longitude) {
        SymptomAnalyzeResponse result = parser.parse(symptomAnalyzePort.analyze(text));

        List<String> deptList =
                result.recommended_specialties()
                        .stream()
                        .map(RecommendedSpecialty::name)
                        .toList();

        List<List<HospitalBaseDTO>> lists = hospitalDomainService.findHospitalsBySymptoms(deptList, latitude, longitude).stream()
                .map(l -> l.stream()
                        .map(mapper::toBaseDto)
                        .toList())
                .toList();


        // 4️⃣ 기존 RecommendedSpecialty 객체를 새로운 hospitals 필드와 함께 생성
        List<RecommendedSpecialty> updatedSpecialties = IntStream.range(0, result.recommended_specialties().size())
                .mapToObj(i -> {
                    RecommendedSpecialty orig = result.recommended_specialties().get(i);
                    List<HospitalBaseDTO> hospitals = i < lists.size() ? lists.get(i) : List.of();
                    return new RecommendedSpecialty(orig.name(), orig.reasons(), hospitals);
                })
                .toList();

        // 5️⃣ 새 SymptomAnalyzeResponse 객체 생성
        return new SymptomAnalyzeResponse(updatedSpecialties);
    }
}
