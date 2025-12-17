package com.hp.hospin.hospital.batch.itemWriter;

import com.hp.hospin.hospital.infrastructure.entity.JpaHospitalDetailEntity;
import jakarta.persistence.EntityManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HospitalDepartmentWriter implements ItemWriter<JpaHospitalDetailEntity> {
    @Autowired
    private EntityManager entityManager;

    @Override
    public void write(Chunk<? extends JpaHospitalDetailEntity> chunk) throws Exception {

        for (JpaHospitalDetailEntity entity : chunk) {
            try {
                entityManager.merge(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
