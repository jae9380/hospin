package com.hp.hospin.batch.listener;

import com.hp.hospin.batch.listener.cache.HospitalCodeCache;
import com.hp.hospin.batch.listener.cache.HospitalDetailCache;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.mapper.HospitalPersistenceMapper;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalDetailJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalDetailJobListener implements JobExecutionListener {
    private final HospitalDetailCache detailCache;
    private final HospitalCodeCache codeCache;
    private final HospitalDetailJPARepository hospitalDetailJPARepository;
    private final HospitalPersistenceMapper mapper;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // hospital_department.csv가 유일한 진료과 출처 — 배치 시작 전 전체 초기화
        hospitalDetailJPARepository.clearAllDepartmentCodes();
        log.info("진료과 코드 전체 초기화 완료 (hospital_department.csv 단일 출처 보장)");

        List<JpaHospitalDetailEntity> existing = hospitalDetailJPARepository.findAll();
        existing.forEach(entity -> detailCache.put(mapper.toDomain(entity)));
        log.info("기존 병원 상세 캐시 preload 완료: {}건", existing.size());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        detailCache.clear();
        codeCache.clear();
        log.info("병원 캐시 초기화 완료");
    }
}
