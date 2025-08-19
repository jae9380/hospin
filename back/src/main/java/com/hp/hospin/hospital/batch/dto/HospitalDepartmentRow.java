package com.hp.hospin.hospital.batch.dto;

public class HospitalDepartmentRow {
    private String hospitalCode;
    private String hospitalName;
    private String departmentCode;

    public HospitalDepartmentRow(String hospitalCode, String hospitalName, String departmentCode) {
        this.hospitalCode = hospitalCode;
        this.hospitalName = hospitalName;
        this.departmentCode = departmentCode;
    }

    public String getHospitalCode() {
        return hospitalCode;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }
}