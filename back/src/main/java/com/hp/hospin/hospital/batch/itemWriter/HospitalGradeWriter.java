package com.hp.hospin.hospital.batch.itemWriter;

import com.hp.hospin.hospital.infrastructure.entity.HospitalGrade;
import jakarta.persistence.EntityManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HospitalGradeWriter implements ItemWriter<HospitalGrade> {
    @Autowired
    private EntityManager entityManager;

    @Override
    public void write(Chunk<? extends HospitalGrade> chunk) throws Exception {
        for (HospitalGrade entity : chunk) {
            entityManager.persist(entity);
        }
    }
}
