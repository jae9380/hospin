package com.hp.hospin.hospital.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HospitalDetailDTO {
    private List<String> departmentCodes;
    private String closedSunday;
    private String closedHoliday;
    private String emergencyDayYn;
    private String emergencyDayPhone1;
    private String emergencyDayPhone2;
    private String emergencyNightYn;
    private String emergencyNightPhone1;
    private String emergencyNightPhone2;
    private String lunchWeekday;
    private String lunchSaturday;
    private String receptionWeekday;
    private String receptionSaturday;
    private String treatSunStart;
    private String treatSunEnd;
    private String treatMonStart;
    private String treatMonEnd;
    private String treatTueStart;
    private String treatTueEnd;
    private String treatWedStart;
    private String treatWedEnd;
    private String treatThuStart;
    private String treatThuEnd;
    private String treatFriStart;
    private String treatFriEnd;
    private String treatSatStart;
    private String treatSatEnd;
}
