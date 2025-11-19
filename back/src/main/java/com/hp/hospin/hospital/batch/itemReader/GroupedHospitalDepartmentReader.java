package com.hp.hospin.hospital.batch.itemReader;

import com.hp.hospin.hospital.batch.dto.HospitalCodeWithDepartments;
import com.hp.hospin.hospital.batch.dto.HospitalDepartmentRow;
import com.hp.hospin.hospital.batch.fieldSetMapper.HospitalDepartmentFieldSetMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GroupedHospitalDepartmentReader implements ItemReader<HospitalCodeWithDepartments>, InitializingBean {

    private FlatFileItemReader<HospitalDepartmentRow> delegate;
    private HospitalDepartmentRow nextRow;

    @Override
    public void afterPropertiesSet() {
        this.delegate = new FlatFileItemReaderBuilder<HospitalDepartmentRow>()
                .name("groupedHospitalDepartmentReader")
                .resource(new ClassPathResource("/csv/hospital_department.csv"))
                .linesToSkip(1) // 테스트 시엔 1, 실제 구동 시엔 3815507
                .delimited().delimiter(",")
                .names("hospitalCode", "hospitalName", "departmentCode", "departmentName", "specialistCount", "optionalDoctorCount")
                .fieldSetMapper(new HospitalDepartmentFieldSetMapper())
                .build();

        // FlatFileItemReader 수동 초기화 (Batch가 직접 관리 안 하므로)
        this.delegate.open(new ExecutionContext());
    }

    @Override
    public HospitalCodeWithDepartments read() throws Exception {
        if (nextRow == null) {
            nextRow = delegate.read();
            if (nextRow == null) return null;
        }

        String currentCode = nextRow.getHospitalCode();
        String currentName = nextRow.getHospitalName();
        List<String> deptList = new ArrayList<>();
        deptList.add(nextRow.getDepartmentCode());

        while (true) {
            HospitalDepartmentRow row = delegate.read();
            if (row == null) {
                nextRow = null;
                break;
            }
            if (!row.getHospitalCode().equals(currentCode)) {
                nextRow = row;
                break;
            }
            deptList.add(row.getDepartmentCode());
        }

        return new HospitalCodeWithDepartments(currentCode, currentName, deptList);
    }
}