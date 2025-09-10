package com.hp.hospin.hospital.domain.type;

import lombok.Getter;

import java.util.List;

@Getter
public class HospitalDetail {
    private final String hospitalCode;
    private final List<String> departmentCodes;

    private final String closedSunday;
    private final String closedHoliday;

    private final String emergencyDayYn;
    private final String emergencyDayPhone1;
    private final String emergencyDayPhone2;

    private final String emergencyNightYn;
    private final String emergencyNightPhone1;
    private final String emergencyNightPhone2;

    private final String lunchWeekday;
    private final String lunchSaturday;

    private final String receptionWeekday;
    private final String receptionSaturday;

    private final String treatSunStart;
    private final String treatSunEnd;

    private final String treatMonStart;
    private final String treatMonEnd;

    private final String treatTueStart;
    private final String treatTueEnd;

    private final String treatWedStart;
    private final String treatWedEnd;

    private final String treatThuStart;
    private final String treatThuEnd;

    private final String treatFriStart;
    private final String treatFriEnd;

    private final String treatSatStart;
    private final String treatSatEnd;

    public HospitalDetail(
            String hospitalCode, List<String> departmentCodes,
            String closedSunday, String closedHoliday,
            String emergencyDayYn, String emergencyDayPhone1, String emergencyDayPhone2,
            String emergencyNightYn, String emergencyNightPhone1, String emergencyNightPhone2,
            String lunchWeekday, String lunchSaturday,
            String receptionWeekday, String receptionSaturday,
            String treatSunStart, String treatSunEnd,
            String treatMonStart, String treatMonEnd,
            String treatTueStart, String treatTueEnd,
            String treatWedStart, String treatWedEnd,
            String treatThuStart, String treatThuEnd,
            String treatFriStart, String treatFriEnd,
            String treatSatStart, String treatSatEnd
    ) {
        this.hospitalCode = hospitalCode;
        this.departmentCodes = departmentCodes;
        this.closedSunday = closedSunday;
        this.closedHoliday = closedHoliday;
        this.emergencyDayYn = emergencyDayYn;
        this.emergencyDayPhone1 = emergencyDayPhone1;
        this.emergencyDayPhone2 = emergencyDayPhone2;
        this.emergencyNightYn = emergencyNightYn;
        this.emergencyNightPhone1 = emergencyNightPhone1;
        this.emergencyNightPhone2 = emergencyNightPhone2;
        this.lunchWeekday = lunchWeekday;
        this.lunchSaturday = lunchSaturday;
        this.receptionWeekday = receptionWeekday;
        this.receptionSaturday = receptionSaturday;
        this.treatSunStart = treatSunStart;
        this.treatSunEnd = treatSunEnd;
        this.treatMonStart = treatMonStart;
        this.treatMonEnd = treatMonEnd;
        this.treatTueStart = treatTueStart;
        this.treatTueEnd = treatTueEnd;
        this.treatWedStart = treatWedStart;
        this.treatWedEnd = treatWedEnd;
        this.treatThuStart = treatThuStart;
        this.treatThuEnd = treatThuEnd;
        this.treatFriStart = treatFriStart;
        this.treatFriEnd = treatFriEnd;
        this.treatSatStart = treatSatStart;
        this.treatSatEnd = treatSatEnd;
    }
}
