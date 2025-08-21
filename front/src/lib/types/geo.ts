export interface RegionOption {
  code: string;   // 시도 코드
  label: string;  // 시도 명칭
}

export interface DistrictOption {
  code: string;     // 시군구 코드
  parent: string;   // 상위 시도 코드
  label: string;    // 시군구 명칭
}

export interface CategoryOption {
  code: string;   // 종별 코드
  label: string;  // 종별 명칭
}
