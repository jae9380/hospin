package com.hp.hospin.notification.infrastructure.mapper;

import com.hp.hospin.notification.domain.entity.FCM;
import com.hp.hospin.notification.infrastructure.entity.JpaFCM;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface FcmPersistenceMapper {
    FcmPersistenceMapper INSTANCE = Mappers.getMapper(FcmPersistenceMapper.class);

    // Domain -> JPA
    JpaFCM toJpaEntity(FCM domain);

    // JPA -> Domain
    FCM toDomainEntity(JpaFCM jpa);
}
