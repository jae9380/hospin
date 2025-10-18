package com.hp.hospin.schedule.presentation;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.global.standard.base.Empty;
import com.hp.hospin.global.common.MemberDetails;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.presentation.dto.CreateScheduleRequest;
import com.hp.hospin.schedule.presentation.dto.ScheduleResponse;
import com.hp.hospin.schedule.presentation.dto.UpdateScheduleRequest;
import com.hp.hospin.schedule.presentation.mapper.ScheduleDtoMapper;
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
    private final ScheduleDtoMapper mapper;

    @GetMapping()
    public ApiResponse<List<ScheduleResponse>> getScheduleList(@AuthenticationPrincipal MemberDetails memberDetails) {
        List<Schedule> schedules = scheduleService.getScheduleList(memberDetails.getId());
        if (schedules.isEmpty()) return ApiResponse.ok(List.of());
        
        return ApiResponse.ok(schedules.stream()
                .map(mapper::domainToResponse)
                .toList());
    }

    @PostMapping()
    public ApiResponse<Empty> createdSchedule(@AuthenticationPrincipal MemberDetails memberDetails,
                                              @RequestBody CreateScheduleRequest createScheduleRequest) {
        scheduleService.createSchedule(memberDetails.getId(), createScheduleRequest);
        return ApiResponse.created();
    }

    @PutMapping("/{scheduleId}")
    public ApiResponse<Empty> modifySchedule(@AuthenticationPrincipal MemberDetails memberDetails,
                                             @PathVariable Long scheduleId,
                                             @RequestBody UpdateScheduleRequest updateScheduleRequest) {
        scheduleService.modifySchedule(memberDetails.getId(), scheduleId, updateScheduleRequest);
        return ApiResponse.noContent();
    }

    @DeleteMapping("/{scheduleId}")
    public ApiResponse<Empty> deleteSchedule(@AuthenticationPrincipal MemberDetails memberDetails,
                                             @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(memberDetails.getId(), scheduleId);
        return ApiResponse.noContent();
    }
}
