package com.hp.hospin.hospital.domain.type;

import java.util.Arrays;

public enum DeptCode {

    GENERAL("00", "일반의"),
    INTERNAL_MEDICINE("01", "내과"),
    NEUROLOGY("02", "신경과"),
    PSYCHIATRY("03", "정신건강의학과"),
    SURGERY("04", "외과"),
    ORTHOPEDICS("05", "정형외과"),
    NEUROSURGERY("06", "신경외과"),
    CARDIOTHORACIC_SURGERY("07", "심장혈관흉부외과"),
    PLASTIC_SURGERY("08", "성형외과"),
    ANESTHESIOLOGY("09", "마취통증의학과"),
    OBSTETRICS("10", "산부인과"),
    PEDIATRICS("11", "소아청소년과"),
    OPHTHALMOLOGY("12", "안과"),
    ENT("13", "이비인후과"),
    DERMATOLOGY("14", "피부과"),
    UROLOGY("15", "비뇨의학과"),
    RADIOLOGY("16", "영상의학과"),
    RADIATION_ONCOLOGY("17", "방사선종양학과"),
    PATHOLOGY("18", "병리과"),
    LAB_MEDICINE("19", "진단검사의학과"),
    TUBERCULOSIS("20", "결핵과"),
    REHABILITATION("21", "재활의학과"),
    NUCLEAR_MEDICINE("22", "핵의학과"),
    FAMILY_MEDICINE("23", "가정의학과"),
    EMERGENCY("24", "응급의학과"),
    OCCUPATIONAL_MEDICINE("25", "직업환경의학과"),
    PREVENTIVE_MEDICINE("26", "예방의학과"),

    DENTAL_ETC("27", "기타1(치과)"),
    KOREAN_ETC("28", "기타4(한방)"),

    ETC_2("31", "기타2"),
    ETC_2_2("40", "기타2(2)"),
    PUBLIC_HEALTH("41", "보건"),
    ETC_3("42", "기타3"),

    PUBLIC_DENTAL("43", "보건기관치과"),
    PUBLIC_KOREAN("44", "보건기관한방"),

    DENTISTRY("49", "치과"),
    ORAL_SURGERY("50", "구강악안면외과"),
    PROSTHODONTICS("51", "치과보철과"),
    ORTHODONTICS("52", "치과교정과"),
    PEDIATRIC_DENTISTRY("53", "소아치과"),
    PERIODONTICS("54", "치주과"),
    CONSERVATIVE_DENTISTRY("55", "치과보존과"),
    ORAL_MEDICINE("56", "구강내과"),
    DENTAL_RADIOLOGY("57", "영상치의학과"),
    ORAL_PATHOLOGY("58", "구강병리과"),
    PREVENTIVE_DENTISTRY("59", "예방치과"),
    DENTAL_TOTAL("60", "치과소계"),
    INTEGRATED_DENTISTRY("61", "통합치의학과"),

    KOREAN_INTERNAL("80", "한방내과"),
    KOREAN_OBGYN("81", "한방부인과"),
    KOREAN_PEDIATRICS("82", "한방소아과"),
    KOREAN_ENT_SKIN("83", "한방안·이비인후·피부과"),
    KOREAN_NEUROPSYCHIATRY("84", "한방신경정신과"),
    ACUPUNCTURE("85", "침구과"),
    KOREAN_REHABILITATION("86", "한방재활의학과"),
    SASANG("87", "사상체질과"),
    KOREAN_EMERGENCY("88", "한방응급"),
    KOREAN_EMERGENCY_2("89", "한방응급"),
    KOREAN_TOTAL("90", "한방소계");

    private final String code;
    private final String description;

    DeptCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /** 코드로 Enum 찾기 */
    public static DeptCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DeptCode: " + code));
    }
}
