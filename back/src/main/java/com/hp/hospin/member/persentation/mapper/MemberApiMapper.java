package com.hp.hospin.member.persentation.mapper;

import com.hp.hospin.member.application.dto.MemberDTO;
import com.hp.hospin.member.persentation.dto.JoinRequest;
import com.hp.hospin.member.persentation.dto.LoginRequest;
import com.hp.hospin.member.persentation.dto.MemberResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MemberApiMapper {
    MemberApiMapper INSTANCE = Mappers.getMapper(MemberApiMapper.class);

    MemberResponse dtoToResponse(MemberDTO member);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "birth", source = "birth")
    MemberDTO joinRequestToDto(JoinRequest request);

    // LoginRequest -> DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "birth", ignore = true)
    MemberDTO loginRequestToDto(LoginRequest request);

}
