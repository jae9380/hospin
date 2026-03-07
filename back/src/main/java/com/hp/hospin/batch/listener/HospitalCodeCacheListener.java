package com.hp.hospin.batch.listener;

import com.hp.hospin.batch.listener.cache.HospitalCodeCache;
import com.hp.hospin.hospital.infrastructure.repository.jpa.HospitalJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class HospitalCodeCacheListener extends JobExecutionListenerSupport {

    private final HospitalJPARepository hospitalJPARepository;
    private final HospitalCodeCache hospitalCodeCache;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        Set<String> codes = hospitalJPARepository.findAllHospitalCodes();
        hospitalCodeCache.initialize(codes);
    }
}
