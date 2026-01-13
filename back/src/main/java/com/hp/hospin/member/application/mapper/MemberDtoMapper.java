package com.hp.hospin.member.application.mapper;

import com.hp.hospin.member.application.dto.MemberDTO;
import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.form.JoinForm;
import com.hp.hospin.member.domain.form.LoginForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MemberDtoMapper {
    MemberDtoMapper INSTANCE = Mappers.getMapper(MemberDtoMapper.class);

    // Domain -> DTO
    @Mapping(target = "id", source = "id")
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "birth", source = "birth")
    MemberDTO domainToDto(Member member);

    // DTO -> Domain
    @Mapping(target = "id", source = "id")
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "password", ignore = true) // password는 DTO에 없으므로 ignore
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", constant = "2") // 기본 회원 역할
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "birth", source = "birth")
    Member dtoToDomain(MemberDTO response);

    // DTO -> JoinForm
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "birth", source = "birth")
    JoinForm dtoToJoinForm(MemberDTO dto);

    // DTO -> LoginForm
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "password", source = "password")
    LoginForm dtoToLoginForm(MemberDTO dto);
}