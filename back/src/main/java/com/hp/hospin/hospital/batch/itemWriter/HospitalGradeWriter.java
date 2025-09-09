package com.hp.hospin.hospital.batch.itemWriter;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalGradeEntity;
import jakarta.persistence.EntityManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HospitalGradeWriter implements ItemWriter<JpaHospitalGradeEntity> {
    @Autowired
    private EntityManager entityManager;

    @Override
    public void write(Chunk<? extends JpaHospitalGradeEntity> chunk) throws Exception {
        for (JpaHospitalGradeEntity entity : chunk) {
            entityManager.persist(entity);
        }
    }
}
