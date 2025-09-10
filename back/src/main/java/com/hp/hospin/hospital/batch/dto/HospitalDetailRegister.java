package com.hp.hospin.hospital.batch.dto;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;

import java.util.List;

public record HospitalDetailRegister(
        String hospitalCode,
        List<String> departmentCodes,
        String closedSunday,
        String closedHoliday,
        String emergencyDayYn,
        String emergencyDayPhone1,
        String emergencyDayPhone2,
        String emergencyNightYn,
        String emergencyNightPhone1,
        String emergencyNightPhone2,
        String lunchWeekday,
        String lunchSaturday,
        String receptionWeekday,
        String receptionSaturday,
        String treatSunStart,
        String treatSunEnd,
        String treatMonStart,
        String treatMonEnd,
        String treatTueStart,
        String treatTueEnd,
        String treatWedStart,
        String treatWedEnd,
        String treatThuStart,
        String treatThuEnd,
        String treatFriStart,
        String treatFriEnd,
        String treatSatStart,
        String treatSatEnd
) {
    public JpaHospitalDetailEntity to() {
        return JpaHospitalDetailEntity.create(
                hospitalCode, departmentCodes, closedSunday, closedHoliday,
                emergencyDayYn, emergencyDayPhone1, emergencyDayPhone2,
                emergencyNightYn, emergencyNightPhone1, emergencyNightPhone2,
                lunchWeekday, lunchSaturday, receptionWeekday, receptionSaturday,
                treatSunStart, treatSunEnd, treatMonStart, treatMonEnd,
                treatTueStart, treatTueEnd, treatWedStart, treatWedEnd,
                treatThuStart, treatThuEnd, treatFriStart, treatFriEnd,
                treatSatStart, treatSatEnd
        );
    }
}
