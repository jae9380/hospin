package com.hp.hospin.hospital.application.mapper;

import com.hp.hospin.hospital.application.dto.HospitalBaseDTO;
import com.hp.hospin.hospital.application.dto.HospitalDTO;
import com.hp.hospin.hospital.application.dto.HospitalDetailDTO;
import com.hp.hospin.hospital.application.dto.HospitalGradeDTO;
import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalDetail;
import com.hp.hospin.hospital.domain.type.HospitalGrade;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.LinkedHashMap;
import java.util.Map;

@Mapper(
        componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface HospitalDtoMapper {
    HospitalDtoMapper INSTANCE = Mappers.getMapper(HospitalDtoMapper.class);

//    // Domain Entity -> DTO
//    default HospitalDTO hospitalToListResponse(Hospital entity) {
//        if (entity == null) return null;
//
//    }

    HospitalBaseDTO toBaseDto(Hospital h);

    HospitalDetailDTO toDetailDto(HospitalDetail d);

    default HospitalGradeDTO toGradeDto(HospitalGrade g) {
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

        return new HospitalGradeDTO(grades);
    }

    default HospitalDTO toHospitalInfoResponse(Hospital base, HospitalDetail detail, HospitalGrade grade) {
        HospitalBaseDTO baseDto = toBaseDto(base);
        HospitalDetailDTO detailDto = toDetailDto(detail);
        HospitalGradeDTO gradeDto = toGradeDto(grade);

        return HospitalDTO.builder()
                .base(baseDto)
                .detail(detailDto)
                .grade(gradeDto)
                .build();
    }

//    // BaseInfo 변환
//    default HospitalBaseDTO toBaseDto(Hospital h) {
//        if (h == null) return null;
//        return new HospitalBaseDTO(
//                h.getHospitalCode(),
//                h.getName(),
//                h.getAddress(),
//                h.getCallNumber(),
//                h.getLatitude(),
//                h.getLongitude()
//        );
//    }
//
//    // DetailInfo 변환
//    default HospitalDetailDTO toDetailDto(HospitalDetail d) {
//        if (d == null) return null;
//        return new HospitalDetailDTO(
//                d.getDepartmentCodes(),
//                d.getClosedSunday(),
//                d.getClosedHoliday(),
//                d.getEmergencyDayYn(),
//                d.getEmergencyDayPhone1(),
//                d.getEmergencyDayPhone2(),
//                d.getEmergencyNightYn(),
//                d.getEmergencyNightPhone1(),
//                d.getEmergencyNightPhone2(),
//                d.getLunchWeekday(),
//                d.getLunchSaturday(),
//                d.getReceptionWeekday(),
//                d.getReceptionSaturday(),
//                d.getTreatSunStart(), d.getTreatSunEnd(),
//                d.getTreatMonStart(), d.getTreatMonEnd(),
//                d.getTreatTueStart(), d.getTreatTueEnd(),
//                d.getTreatWedStart(), d.getTreatWedEnd(),
//                d.getTreatThuStart(), d.getTreatThuEnd(),
//                d.getTreatFriStart(), d.getTreatFriEnd(),
//                d.getTreatSatStart(), d.getTreatSatEnd()
//        );
//    }

    // GradeInfo 변환: asmGrdXX 문자열들을 Map<String, Long>으로 변환


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