package com.hp.hospin.batch.itemWriter;

import com.hp.hospin.batch.listener.cache.HospitalDetailCache;
import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import com.hp.hospin.hospital.infrastructure.mapper.HospitalPersistenceMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HospitalDetailWriter implements ItemWriter<JpaHospitalDetailEntity> {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private HospitalDetailCache cache;

    private final HospitalPersistenceMapper mapper;

    @Override
    public void write(Chunk<? extends JpaHospitalDetailEntity> chunk) throws Exception {
        for (JpaHospitalDetailEntity entity : chunk) {
            entity.setDepartmentCodes(null);       // hospital_detail.csv는 진료과 출처가 아님을 명시
            entityManager.merge(entity);           // DB 저장
            cache.put(mapper.toDomain(entity));    // Step 2에서 진료과 정보 업데이트용 캐시
        }
    }
}
