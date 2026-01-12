export interface HospitalInfoResponse {
  baseInfo: BaseInfo;
  detailInfo: DetailInfo;
  gradeInfo: GradeInfo;
}

export interface BaseInfo {
  hospitalCode: string;
  name: string;
  address: string;
  callNumber: string;
  latitude: string;   // 백엔드가 string으로 주므로 주의
  longitude: string;  // 백엔드가 string으로 주므로 주의
}

export interface DetailInfo {
  departmentCodes: string[];
  closedSunday: string;
  closedHoliday: string;
  emergencyDayYn: string;
  emergencyDayPhone1: string;
  emergencyDayPhone2: string;
  emergencyNightYn: string;
  emergencyNightPhone1: string;
  emergencyNightPhone2: string;
  lunchWeekday: string;
  lunchSaturday: string;
  receptionWeekday: string;
  receptionSaturday: string;
  treatSunStart: string; treatSunEnd: string;
  treatMonStart: string; treatMonEnd: string;
  treatTueStart: string; treatTueEnd: string;
  treatWedStart: string; treatWedEnd: string;
  treatThuStart: string; treatThuEnd: string;
  treatFriStart: string; treatFriEnd: string;
  treatSatStart: string; treatSatEnd: string;
}

export interface GradeInfo {
  grades: Record<string, number>;
}
