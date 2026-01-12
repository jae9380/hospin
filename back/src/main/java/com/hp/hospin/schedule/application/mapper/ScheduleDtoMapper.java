package com.hp.hospin.schedule.application.mapper;

import com.hp.hospin.schedule.application.dto.ScheduleDTO;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.domain.form.ScheduleForm;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ScheduleDtoMapper {

    ScheduleDtoMapper INSTANCE = Mappers.getMapper(ScheduleDtoMapper.class);

    // Domain Entity -> DTO
    ScheduleDTO toDto(Schedule schedule);

    // DTO -> Domain Entity
    Schedule toEntity(ScheduleDTO dto);

    // DTO -> Form
    default ScheduleForm toForm(ScheduleDTO dto) {
        if (dto == null) return null;
        return new ScheduleForm(
                dto.getCategory(),
                dto.getType(),
                dto.getTitle(),
                dto.getMemo(),
                dto.getStartDatetime(),
                dto.getEndDatetime(),
                dto.getRecurringType(),
                dto.getRecurrenceRule()
        );
    }
}