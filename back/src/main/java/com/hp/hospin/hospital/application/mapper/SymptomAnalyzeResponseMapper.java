//package com.hp.hospin.hospital.application.mapper;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hp.hospin.hospital.application.dto.RecommendedSpecialty;
//import com.hp.hospin.hospital.application.dto.SymptomAnalyzeResponse;
//import org.mapstruct.Mapper;
//import org.mapstruct.ReportingPolicy;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//
///**
// * SymptomAnalyzeResponse Mapper (MapStruct 스타일)
// */
//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
//public interface SymptomAnalyzeResponseMapper {
//
//    SymptomAnalyzeResponseMapper INSTANCE = Mappers.getMapper(SymptomAnalyzeResponseMapper.class);
//
//    ObjectMapper objectMapper = new ObjectMapper();
//
//    /**
//     * JSON 문자열을 SymptomAnalyzeResponse로 변환
//     */
//    default SymptomAnalyzeResponse fromJson(String json) {
//        if (json == null || json.isEmpty()) {
//            return new SymptomAnalyzeResponse(List.of());
//        }
//
//        try {
//            return objectMapper.readValue(json, SymptomAnalyzeResponse.class);
//        } catch (JsonProcessingException e) {
//            // 필요시 로깅 가능
//            e.printStackTrace();
//            return new SymptomAnalyzeResponse(List.of());
//        }
//    }
//
//    /**
//     * JSON 배열 형태를 List<RecommendedSpecialty>로 변환
//     * 필요하면 개별 specialty 처리 가능
//     */
//    default List<RecommendedSpecialty> toRecommendedSpecialtyList(String jsonArray) {
//        if (jsonArray == null || jsonArray.isEmpty()) {
//            return List.of();
//        }
//        try {
//            return objectMapper.readValue(
//                    jsonArray,
//                    objectMapper.getTypeFactory().constructCollectionType(List.class, RecommendedSpecialty.class)
//            );
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return List.of();
//        }
//    }
//}