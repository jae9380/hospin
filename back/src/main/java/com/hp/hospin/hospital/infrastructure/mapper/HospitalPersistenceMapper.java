package com.hp.hospin.hospital.infrastructure.mapper;

import com.hp.hospin.hospital.domain.type.Hospital;
import com.hp.hospin.hospital.domain.type.HospitalDetail;
import com.hp.hospin.hospital.domain.type.HospitalGrade;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalEntity;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HospitalPersistenceMapper {
    HospitalPersistenceMapper INSTANCE = Mappers.getMapper(HospitalPersistenceMapper.class);
    Hospital toDomain(JpaHospitalEntity entity);
    JpaHospitalEntity toJpaEntity(Hospital hospital);

    HospitalDetail toDomain(JpaHospitalDetailEntity entity);
    JpaHospitalDetailEntity toJpaEntity(HospitalDetail detail);

    HospitalGrade toDomain(JpaHospitalGradeEntity entity);
    JpaHospitalGradeEntity toJpaEntity(HospitalGrade grade);
}
