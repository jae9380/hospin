package com.hp.hospin.hospital.batch.itemWriter;

import com.hp.hospin.hospital.entity.HospitalDetail;
import jakarta.persistence.EntityManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HospitalDetailWriter implements ItemWriter<HospitalDetail> {
    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void write(Chunk<? extends HospitalDetail> chunk) throws Exception {
        try {
            for (HospitalDetail entity : chunk) {
                entityManager.persist(entity);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
