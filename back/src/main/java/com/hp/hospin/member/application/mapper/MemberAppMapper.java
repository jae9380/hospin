package com.hp.hospin.member.application.mapper;

import com.hp.hospin.member.application.dto.MemberResponse;
import com.hp.hospin.member.domain.entity.Member;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MemberAppMapper {
    MemberAppMapper INSTANCE = Mappers.getMapper(MemberAppMapper.class);

    MemberResponse domainToDto(Member member);

}
