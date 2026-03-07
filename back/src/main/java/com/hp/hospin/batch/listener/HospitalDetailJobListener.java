package com.hp.hospin.batch.listener;

import com.hp.hospin.batch.listener.cache.HospitalCodeCache;
import com.hp.hospin.batch.listener.cache.HospitalDetailCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalDetailJobListener implements JobExecutionListener {
    private final HospitalDetailCache detailCache;
    private final HospitalCodeCache codeCache;

    @Override
    public void afterJob(JobExecution jobExecution) {
        detailCache.clear();
        codeCache.clear();
        log.info("병원 캐시 초기화 완료");
    }
}
