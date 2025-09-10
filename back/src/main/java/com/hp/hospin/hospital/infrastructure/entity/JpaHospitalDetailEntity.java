package com.hp.hospin.hospital.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity(name="hospital_detail")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "hospital_detail")
public class JpaHospitalDetailEntity {
    @Id
    @Column(name = "hospital_code", nullable = false, unique = true)
    private String hospitalCode;

    @Convert(converter = DepartmentCodeListConverter.class)
    @Column(name = "department_code")   // 진료 과목 코드
    private List<String> departmentCodes;

    @Column(name = "closedSunday")
    private String closedSunday;          // 휴진안내_일요일

    @Column(name = "closedHoliday")
    private String closedHoliday;         // 휴진안내_공휴일

    @Column(name = "emergencyDayYn")
    private String emergencyDayYn;        // 응급실_주간_운영여부

    @Column(name = "emergencyDayPhone1")
    private String emergencyDayPhone1;    // 응급실_주간_전화번호1

    @Column(name = "emergencyDayPhone2")
    private String emergencyDayPhone2;    // 응급실_주간_전화번호2

    @Column(name = "emergencyNightYn")
    private String emergencyNightYn;      // 응급실_야간_운영여부

    @Column(name = "emergencyNightPhone1")
    private String emergencyNightPhone1;  // 응급실_야간_전화번호1

    @Column(name = "emergencyNightPhone2")
    private String emergencyNightPhone2;  // 응급실_야간_전화번호2

    @Column(name = "lunchWeekday")
    private String lunchWeekday;          // 점심시간_평일

    @Column(name = "lunchSaturday")
    private String lunchSaturday;         // 점심시간_토요일

    @Column(name = "receptionWeekday")
    private String receptionWeekday;      // 접수시간_평일

    @Column(name = "receptionSaturday")
    private String receptionSaturday;     // 접수시간_토요일

    @Column(name = "treatSunStart")
    private String treatSunStart;         // 진료시작시간_일요일

    @Column(name = "treatSunEnd")
    private String treatSunEnd;           // 진료종료시간_일요일

    @Column(name = "treatMonStart")
    private String treatMonStart;         // 진료시작시간_월요일

    @Column(name = "treatMonEnd")
    private String treatMonEnd;           // 진료종료시간_월요일

    @Column(name = "treatTueStart")
    private String treatTueStart;         // 진료시작시간_화요일

    @Column(name = "treatTueEnd")
    private String treatTueEnd;           // 진료종료시간_화요일

    @Column(name = "treatWedStart")
    private String treatWedStart;         // 진료시작시간_수요일

    @Column(name = "treatWedEnd")
    private String treatWedEnd;           // 진료종료시간_수요일

    @Column(name = "treatThuStart")
    private String treatThuStart;         // 진료시작시간_목요일

    @Column(name = "treatThuEnd")
    private String treatThuEnd;           // 진료종료시간_목요일

    @Column(name = "treatFriStart")
    private String treatFriStart;         // 진료시작시간_금요일

    @Column(name = "treatFriEnd")
    private String treatFriEnd;           // 진료종료시간_금요일

    @Column(name = "treatSatStart")
    private String treatSatStart;         // 진료시작시간_토요일

    @Column(name = "treatSatEnd")
    private String treatSatEnd;           // 진료종료시간_토요일

    private JpaHospitalDetailEntity(
            String hospitalCode, List<String> departmentCodes,
            String closedSunday, String closedHoliday, String emergencyDayYn,
            String emergencyDayPhone1, String emergencyDayPhone2, String emergencyNightYn,
            String emergencyNightPhone1, String emergencyNightPhone2, String lunchWeekday,
            String lunchSaturday, String receptionWeekday, String receptionSaturday, String treatSunStart,
            String treatSunEnd, String treatMonStart, String treatMonEnd, String treatTueStart,
            String treatTueEnd, String treatWedStart, String treatWedEnd, String treatThuStart,
            String treatThuEnd, String treatFriStart, String treatFriEnd, String treatSatStart, String treatSatEnd
    ) {
        this.hospitalCode = hospitalCode;this.departmentCodes = departmentCodes;
        this.closedSunday = closedSunday;this.closedHoliday = closedHoliday;
        this.emergencyDayYn = emergencyDayYn;
        this.emergencyDayPhone1 = emergencyDayPhone1;this.emergencyDayPhone2 = emergencyDayPhone2;
        this.emergencyNightYn = emergencyNightYn;
        this.emergencyNightPhone1 = emergencyNightPhone1;this.emergencyNightPhone2 = emergencyNightPhone2;
        this.lunchWeekday = lunchWeekday;this.lunchSaturday = lunchSaturday;
        this.receptionWeekday = receptionWeekday;this.receptionSaturday = receptionSaturday;
        this.treatSunStart = treatSunStart;this.treatSunEnd = treatSunEnd;
        this.treatMonStart = treatMonStart;this.treatMonEnd = treatMonEnd;
        this.treatTueStart = treatTueStart;this.treatTueEnd = treatTueEnd;
        this.treatWedStart = treatWedStart;this.treatWedEnd = treatWedEnd;
        this.treatThuStart = treatThuStart;this.treatThuEnd = treatThuEnd;
        this.treatFriStart = treatFriStart;this.treatFriEnd = treatFriEnd;
        this.treatSatStart = treatSatStart;this.treatSatEnd = treatSatEnd;
    }

    public static JpaHospitalDetailEntity create(
            String hospitalCode, List<String> departmentCodes,
            String closedSunday, String closedHoliday, String emergencyDayYn,
            String emergencyDayPhone1, String emergencyDayPhone2, String emergencyNightYn,
            String emergencyNightPhone1, String emergencyNightPhone2, String lunchWeekday,
            String lunchSaturday, String receptionWeekday, String receptionSaturday, String treatSunStart,
            String treatSunEnd, String treatMonStart, String treatMonEnd, String treatTueStart,
            String treatTueEnd, String treatWedStart, String treatWedEnd, String treatThuStart,
            String treatThuEnd, String treatFriStart, String treatFriEnd, String treatSatStart, String treatSatEnd
    ) {
        return new JpaHospitalDetailEntity(
                hospitalCode,
                (departmentCodes == null || departmentCodes.isEmpty()) ? null : departmentCodes,
                closedSunday,
                closedHoliday,
                emergencyDayYn,
                emergencyDayPhone1,
                emergencyDayPhone2,
                emergencyNightYn,
                emergencyNightPhone1,
                emergencyNightPhone2,
                lunchWeekday,
                lunchSaturday,
                receptionWeekday,
                receptionSaturday,
                treatSunStart,
                treatSunEnd,
                treatMonStart,
                treatMonEnd,
                treatTueStart,
                treatTueEnd,
                treatWedStart,
                treatWedEnd,
                treatThuStart,
                treatThuEnd,
                treatFriStart,
                treatFriEnd,
                treatSatStart,
                treatSatEnd
        );
    }

    public void addDepartmentCodes(List<String> newCodes) {
        if (newCodes == null || newCodes.isEmpty()) return;

        if (this.departmentCodes == null) {
            this.departmentCodes = new ArrayList<>(newCodes);
        } else {
            Set<String> merged = new HashSet<>(this.departmentCodes);
            merged.addAll(newCodes);
            this.departmentCodes = new ArrayList<>(merged); // 순서 유지를 원하면 LinkedHashSet 사용
        }
    }
}