package com.hp.hospin.member.infrastructure.mapper;

import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.infrastructure.entity.JpaMemberEntity;
import com.hp.hospin.member.infrastructure.type.Gender;
import com.hp.hospin.member.infrastructure.type.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MemberPersistenceMapper {
    MemberPersistenceMapper INSTANCE = Mappers.getMapper(MemberPersistenceMapper.class);

    // JPA -> Domain
    @Mapping(target = "role", expression = "java(roleToCode(entity.getRole()))")
    @Mapping(target = "gender", expression = "java(genderToCode(entity.getGender()))")
    Member jpaToDomain(JpaMemberEntity entity);

    // Domain -> JPA
    @Mapping(target = "role", expression = "java(codeToRole(member.getRole()))")
    @Mapping(target = "gender", expression = "java(codeToGender(member.getGender()))")
    JpaMemberEntity domainToJpa(Member member);

    // Domain(int) -> JPA(Enum)
    default Role codeToRole(int code) {
        return Role.fromCode(code);
    }

    default Gender codeToGender(int code) {
        return Gender.fromCode(code);
    }

    // JPA(Enum) -> Domain(int)
    default int roleToCode(Role role) {
        return role.getCode();
    }

    default int genderToCode(Gender gender) {
        return gender.getCode();
    }
}