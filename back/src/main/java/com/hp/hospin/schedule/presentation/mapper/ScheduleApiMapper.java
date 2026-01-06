package com.hp.hospin.schedule.presentation.mapper;

import com.hp.hospin.schedule.application.dto.ScheduleDTO;
import com.hp.hospin.schedule.presentation.dto.ScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.ScheduleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ScheduleApiMapper {

    ScheduleApiMapper INSTANCE = Mappers.getMapper(ScheduleApiMapper.class);

    ScheduleResponse domainToResponse(ScheduleDTO schedule);

    default ScheduleDTO requestToDomain(ScheduleRequest request) {
        return new ScheduleDTO(request);
    }
}