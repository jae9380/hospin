package com.hp.hospin.hospital.presentation.mapper;

import com.hp.hospin.hospital.application.dto.HospitalBaseDTO;
import com.hp.hospin.hospital.application.dto.HospitalDTO;
import com.hp.hospin.hospital.application.dto.HospitalDetailDTO;
import com.hp.hospin.hospital.application.dto.HospitalGradeDTO;
import com.hp.hospin.hospital.presentation.dto.HospitalInfoResponse;
import com.hp.hospin.hospital.presentation.dto.HospitalListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface HospitalApiMapper {
    HospitalApiMapper INSTANCE = Mappers.getMapper(HospitalApiMapper.class);

    // Request -> DTO

    // DTO -> Response
    @Mapping(target = "hospital_code", source = "hospitalCode")
    HospitalListResponse toListResponse(HospitalBaseDTO baseDto);

    @Mapping(source = "base", target = "baseInfo")
    @Mapping(source = "detail", target = "detailInfo")
    @Mapping(source = "grade", target = "gradeInfo")
    HospitalInfoResponse toInfoResponse(HospitalDTO dto);

    HospitalInfoResponse.BaseInfo toBaseInfo(HospitalBaseDTO dto);

    HospitalInfoResponse.DetailInfo toDetailInfo(HospitalDetailDTO dto);

    HospitalInfoResponse.GradeInfo toGradeInfo(HospitalGradeDTO dto);
}
