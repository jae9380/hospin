package com.hp.hospin.schedule.presentation;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.global.standard.annotations.Monitored;
import com.hp.hospin.global.standard.base.Empty;
import com.hp.hospin.global.common.MemberDetails;
import com.hp.hospin.schedule.application.dto.ScheduleDTO;
import com.hp.hospin.schedule.presentation.dto.ScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.ScheduleResponse;
import com.hp.hospin.schedule.presentation.mapper.ScheduleApiMapper;
import com.hp.hospin.schedule.presentation.port.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ScheduleApiMapper mapper;

    @GetMapping()
    @Monitored(
            domain = "schedule",
            layer = "presentation",
            api = "getScheduleList"
    )
    public ApiResponse<List<ScheduleResponse>> getScheduleList(@AuthenticationPrincipal MemberDetails memberDetails) {
        List<ScheduleDTO> schedules = scheduleService.getScheduleList(memberDetails.getId());
        if (schedules.isEmpty()) return ApiResponse.ok(List.of());
        return ApiResponse.ok(schedules.stream()
                .map(mapper::dtoToResponse)
                .toList());
    }

    @PostMapping()
    @Monitored(
            domain = "schedule",
            layer = "presentation",
            api = "createdSchedule"
    )
    public ApiResponse<ScheduleResponse> createdSchedule(@AuthenticationPrincipal MemberDetails memberDetails,
                                              @RequestBody ScheduleRequest createScheduleRequest) {
        ScheduleResponse schedule =  mapper.dtoToResponse(
                scheduleService.createSchedule(memberDetails.getId(), mapper.requestToDomain(createScheduleRequest))
        );
        return ApiResponse.created(schedule);
    }

    @PutMapping("/{scheduleId}")
    @Monitored(
            domain = "schedule",
            layer = "presentation",
            api = "modifySchedule"
    )
    public ApiResponse<ScheduleResponse> modifySchedule(@AuthenticationPrincipal MemberDetails memberDetails,
                                             @PathVariable Long scheduleId,
                                             @RequestBody ScheduleRequest updateScheduleRequest) {
        ScheduleResponse schdule =  mapper.dtoToResponse(
                scheduleService.modifySchedule(memberDetails.getId(), scheduleId, mapper.requestToDomain(updateScheduleRequest))
        );
        return ApiResponse.ok(schdule);
    }

    @DeleteMapping("/{scheduleId}")
    @Monitored(
            domain = "schedule",
            layer = "presentation",
            api = "deleteSchedule"
    )
    public ApiResponse<Empty> deleteSchedule(@AuthenticationPrincipal MemberDetails memberDetails,
                                             @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(memberDetails.getId(), scheduleId);
        return ApiResponse.noContent();
    }

    @GetMapping("/getClosestSchedule")
    @Monitored(
            domain = "schedule",
            layer = "presentation",
            api = "getClosestSchedule"
    )
    public ApiResponse<List<ScheduleResponse>> getClosestSchedule(@AuthenticationPrincipal MemberDetails memberDetails) {
        List<ScheduleDTO> schedules = scheduleService.getClosestSchedule(memberDetails.getId());
        if (schedules.isEmpty()) return ApiResponse.ok(List.of());
        return ApiResponse.ok(schedules.stream()
                .map(mapper::dtoToResponse)
                .toList());
    }

}
