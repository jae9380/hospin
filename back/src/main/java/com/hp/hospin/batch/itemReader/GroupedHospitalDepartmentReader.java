package com.hp.hospin.batch.itemReader;

import com.hp.hospin.batch.dto.HospitalCodeWithDepartments;
import com.hp.hospin.batch.dto.HospitalDepartmentRow;
import com.hp.hospin.batch.fieldSetMapper.HospitalDepartmentFieldSetMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class GroupedHospitalDepartmentReader implements ItemReader<HospitalCodeWithDepartments>, ItemStream {

    private Iterator<HospitalCodeWithDepartments> groupedIterator;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        FlatFileItemReader<HospitalDepartmentRow> csvReader = new FlatFileItemReaderBuilder<HospitalDepartmentRow>()
                .name("groupedHospitalDepartmentReader")
                .resource(new ClassPathResource("/csv/hospital_department.csv"))
                .linesToSkip(1)
                .delimited().delimiter(",")
                .names("hospitalCode", "hospitalName", "departmentCode", "departmentName", "specialistCount", "optionalDoctorCount")
                .fieldSetMapper(new HospitalDepartmentFieldSetMapper())
                .build();

        csvReader.open(executionContext);

        // LinkedHashMap으로 전체 CSV를 읽어 hospitalCode 기준 그룹핑
        // 기존 방식(연속 행만 그룹핑)은 CSV 미정렬 시 동일 코드가 여러 그룹으로 분리되어 유실 발생
        Map<String, HospitalCodeWithDepartments> grouped = new LinkedHashMap<>();
        try {
            HospitalDepartmentRow row;
            while ((row = csvReader.read()) != null) {
                String code = row.getHospitalCode();
                String name = row.getHospitalName();
                String deptCode = row.getDepartmentCode();
                grouped.computeIfAbsent(code,
                        k -> new HospitalCodeWithDepartments(k, name, new ArrayList<>()))
                        .getDepartmentCodes().add(deptCode);
            }
        } catch (Exception e) {
            throw new ItemStreamException("hospital_department.csv 읽기 중 오류 발생", e);
        } finally {
            csvReader.close();
        }

        this.groupedIterator = grouped.values().iterator();
        log.info("진료과 데이터 그룹핑 완료: {}개 병원", grouped.size());
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // 인메모리 이터레이터 기반이므로 상태 저장 불필요
    }

    @Override
    public void close() throws ItemStreamException {
        this.groupedIterator = null;
    }

    @Override
    public HospitalCodeWithDepartments read() throws Exception {
        if (groupedIterator != null && groupedIterator.hasNext()) {
            return groupedIterator.next();
        }
        return null;
    }
}
