// 백엔드가 snake_case/camelCase 중 무엇으로 내려올지 모를 때를 대비한 원본 타입
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
  // 필요하면 아래 필드도 추가해 사용하세요
  // number?: number;
  // size?: number;
  // first?: boolean;
  // last?: boolean;
}
