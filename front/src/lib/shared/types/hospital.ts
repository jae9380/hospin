// --- hospital.ts (기존 types/hospital/hospital.ts) ---
export type Hospital = {
	hospital_code: string;
	name: string;
	address: string;
	latitude: number;
	longitude: number;
};

// --- info.ts (기존 types/hospital/info.ts) ---
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

// --- list.ts (기존 types/hospital/list.ts) ---
export interface HospitalListItemRaw {
	hospitalCode?: string;
	hospital_code?: string;
	name: string;
	address: string;
	callNumber?: string | null;
}

// 프론트에서 일관되게 쓸 표준화 타입
export interface HospitalListItem {
	hospitalCode: string;
	name: string;
	address: string;
	callNumber?: string | null;
}

// 표준화 유틸: raw → normalized
export function normalizeHospitalListItem(raw: HospitalListItemRaw): HospitalListItem {
	return {
		hospitalCode: raw.hospitalCode ?? raw.hospital_code ?? '',
		name: raw.name,
		address: raw.address,
		callNumber: raw.callNumber ?? null
	};
}

// Spring Data Page 응답 최소 형태 제너릭
export interface PageResult<T> {
	content: T[];
	totalPages: number;
	totalElements: number;
	// number?: number;
	// size?: number;
	// first?: boolean;
	// last?: boolean;
}

export interface HospitalListResponse {
	hospital_code: string;
	name: string;
	address: string;
	latitude: number;
	longitude: number;
}
