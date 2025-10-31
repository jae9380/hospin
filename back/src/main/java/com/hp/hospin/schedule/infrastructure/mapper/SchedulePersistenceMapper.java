package com.hp.hospin.schedule.infrastructure.mapper;

import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.infrastructure.entity.JpaScheduleEntity;
import com.hp.hospin.schedule.infrastructure.entity.type.RecurringType;
import com.hp.hospin.schedule.infrastructure.entity.type.ScheduleCategoryType;
import com.hp.hospin.schedule.infrastructure.entity.type.ScheduleType;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SchedulePersistenceMapper {
    SchedulePersistenceMapper INSTANCE = Mappers.getMapper(SchedulePersistenceMapper.class);
    // JPA -> Domain
    @Mapping(target = "category", expression = "java(categoryToCode(entity.getCategory()))")
    @Mapping(target = "type",     expression = "java(typeToCode(entity.getType()))")
    @Mapping(target = "recurringType", expression = "java(recurringTypeToCode(entity.getRecurringType()))")
    Schedule jpaToDomain(JpaScheduleEntity entity);

    // Domain -> JPA
    @Mapping(target = "category", expression = "java(codeToCategory(schedule.getCategory()))")
    @Mapping(target = "type",     expression = "java(codeToType(schedule.getType()))")
    @Mapping(target = "recurringType", expression = "java(codeToRecurringType(schedule.getRecurringType()))")
    JpaScheduleEntity domainToJpa(Schedule schedule);

    // --- converters: Enum <-> Long(code)
    default int categoryToCode(ScheduleCategoryType e){
        return e.getCode();
    }
    default int typeToCode(ScheduleType e){
        return e.getCode();
    }
    default int recurringTypeToCode(RecurringType e){
        return e.getCode();
    }

    default ScheduleCategoryType codeToCategory(int code){
        return ScheduleCategoryType.fromCode(code);
    }
    default ScheduleType codeToType(int code){
        return ScheduleType.fromCode(code);
    }
    default RecurringType codeToRecurringType(int code){
        return RecurringType.fromCode(code);
    }
}
