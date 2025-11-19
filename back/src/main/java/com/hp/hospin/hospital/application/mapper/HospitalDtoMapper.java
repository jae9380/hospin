package com.hp.hospin.hospital.application.mapper;

import com.hp.hospin.hospital.application.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.application.dto.HospitalInfoResponse.*;
import com.hp.hospin.hospital.application.dto.HospitalListResponse;
import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalDetail;
import com.hp.hospin.hospital.domain.type.HospitalGrade;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Hospital 관련 DTO 변환기 (MapStruct 스타일로 정의, 복합 변환은 default 메서드로 처리)
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HospitalDtoMapper {
    HospitalDtoMapper INSTANCE = Mappers.getMapper(HospitalDtoMapper.class);

    // 간단한 단일 엔티티 -> 리스트 응답 변환 (정적 팩토리 사용)
    default HospitalListResponse hospitalToListResponse(Hospital entity) {
        if (entity == null) return null;
        return HospitalListResponse.from(entity);
    }

    // --- 복합 응답: BaseInfo / DetailInfo / GradeInfo를 조립하여 HospitalInfoResponse 반환 ---
    default HospitalInfoResponse toHospitalInfoResponse(Hospital base, HospitalDetail detail, HospitalGrade grade) {
        BaseInfo baseInfo = toBaseInfo(base);
        DetailInfo detailInfo = toDetailInfo(detail);
        GradeInfo gradeInfo = toGradeInfo(grade);

        return HospitalInfoResponse.builder()
                .baseInfo(baseInfo)
                .detailInfo(detailInfo)
                .gradeInfo(gradeInfo)
                .build();
    }

    // BaseInfo 변환
    default BaseInfo toBaseInfo(Hospital h) {
        if (h == null) return null;
        return new BaseInfo(
                h.getHospitalCode(),
                h.getName(),
                h.getAddress(),
                h.getCallNumber(),
                h.getLatitude(),
                h.getLongitude()
        );
    }

    // DetailInfo 변환
    default DetailInfo toDetailInfo(HospitalDetail d) {
        if (d == null) return null;
        return new DetailInfo(
                d.getDepartmentCodes(),
                d.getClosedSunday(),
                d.getClosedHoliday(),
                d.getEmergencyDayYn(),
                d.getEmergencyDayPhone1(),
                d.getEmergencyDayPhone2(),
                d.getEmergencyNightYn(),
                d.getEmergencyNightPhone1(),
                d.getEmergencyNightPhone2(),
                d.getLunchWeekday(),
                d.getLunchSaturday(),
                d.getReceptionWeekday(),
                d.getReceptionSaturday(),
                d.getTreatSunStart(), d.getTreatSunEnd(),
                d.getTreatMonStart(), d.getTreatMonEnd(),
                d.getTreatTueStart(), d.getTreatTueEnd(),
                d.getTreatWedStart(), d.getTreatWedEnd(),
                d.getTreatThuStart(), d.getTreatThuEnd(),
                d.getTreatFriStart(), d.getTreatFriEnd(),
                d.getTreatSatStart(), d.getTreatSatEnd()
        );
    }

    // GradeInfo 변환: asmGrdXX 문자열들을 Map<String, Long>으로 변환
    default GradeInfo toGradeInfo(HospitalGrade g) {
        if (g == null) return null;

        Map<String, Long> grades = new LinkedHashMap<>();
        // helper: 안전하게 파싱
        grades.put("asmGrd01", safeParseLong(g.getAsmGrd01()));
        grades.put("asmGrd02", safeParseLong(g.getAsmGrd02()));
        grades.put("asmGrd03", safeParseLong(g.getAsmGrd03()));
        grades.put("asmGrd04", safeParseLong(g.getAsmGrd04()));
        grades.put("asmGrd05", safeParseLong(g.getAsmGrd05()));
        grades.put("asmGrd06", safeParseLong(g.getAsmGrd06()));
        grades.put("asmGrd07", safeParseLong(g.getAsmGrd07()));
        grades.put("asmGrd08", safeParseLong(g.getAsmGrd08()));
        grades.put("asmGrd09", safeParseLong(g.getAsmGrd09()));
        grades.put("asmGrd10", safeParseLong(g.getAsmGrd10()));
        grades.put("asmGrd11", safeParseLong(g.getAsmGrd11()));
        grades.put("asmGrd12", safeParseLong(g.getAsmGrd12()));
        grades.put("asmGrd13", safeParseLong(g.getAsmGrd13()));
        grades.put("asmGrd14", safeParseLong(g.getAsmGrd14()));
        grades.put("asmGrd15", safeParseLong(g.getAsmGrd15()));
        grades.put("asmGrd16", safeParseLong(g.getAsmGrd16()));
        grades.put("asmGrd17", safeParseLong(g.getAsmGrd17()));
        grades.put("asmGrd18", safeParseLong(g.getAsmGrd18()));
        grades.put("asmGrd19", safeParseLong(g.getAsmGrd19()));
        grades.put("asmGrd20", safeParseLong(g.getAsmGrd20()));
        grades.put("asmGrd21", safeParseLong(g.getAsmGrd21()));
        grades.put("asmGrd22", safeParseLong(g.getAsmGrd22()));
        grades.put("asmGrd23", safeParseLong(g.getAsmGrd23()));
        grades.put("asmGrd24", safeParseLong(g.getAsmGrd24()));

        return new GradeInfo(grades);
    }

    // 안전 파싱 유틸 (null/빈문자열 처리, 파싱 실패 시 null 반환)
    default Long safeParseLong(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return null;
        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException ex) {
            // 필요하면 여기서 로깅 추가
            return null;
        }
    }
}