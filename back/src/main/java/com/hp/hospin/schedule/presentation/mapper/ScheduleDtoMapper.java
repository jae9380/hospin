package com.hp.hospin.schedule.presentation.mapper;

import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.presentation.dto.CreateScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.ScheduleResponse;
import com.hp.hospin.schedule.presentation.dto.UpdateScheduleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ScheduleDtoMapper {

    ScheduleDtoMapper INSTANCE = Mappers.getMapper(ScheduleDtoMapper.class);

    ScheduleResponse domainToResponse(Schedule schedule);

    default Schedule requestToDomain(CreateScheduleRequest request, Long userId) {
        return Schedule.create(userId, request);
    }
}