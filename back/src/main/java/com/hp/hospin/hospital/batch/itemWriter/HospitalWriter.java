package com.hp.hospin.hospital.batch.itemWriter;

import com.hp.hospin.hospital.infrastructure.entity.Hospital;
import jakarta.persistence.EntityManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HospitalWriter implements ItemWriter<Hospital> {
    @Autowired
    private EntityManager entityManager;

    @Override
    public void write(Chunk<? extends Hospital> chunk) throws Exception {
        for (Hospital entity : chunk) {
            entityManager.persist(entity);
        }
    }
}
