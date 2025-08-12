package com.hp.hospin.hospital.batch.fieldSetMapper;

import com.hp.hospin.hospital.batch.dto.HospitalRegister;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class HospitalFieldSetMapper implements FieldSetMapper<HospitalRegister> {
    @Override
    public HospitalRegister mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) return null;

        HospitalRegister dto = new HospitalRegister(
                fieldSet.readRawString(0),
                fieldSet.readRawString(1),
                fieldSet.readRawString(2),
                fieldSet.readRawString(4),
                fieldSet.readRawString(6),
                fieldSet.readRawString(9),
                fieldSet.readRawString(10),
                fieldSet.readRawString(11),
                fieldSet.readRawString(28),
                fieldSet.readRawString(29)
        );

        return dto;
    }
}
