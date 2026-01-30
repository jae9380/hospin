package com.hp.hospin.notification.infrastructure;

import com.hp.hospin.notification.domain.entity.FCM;
import com.hp.hospin.notification.domain.port.FcmRepository;
import com.hp.hospin.notification.infrastructure.jpa.FcmJpaRepository;
import com.hp.hospin.notification.infrastructure.mapper.FcmPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FcmRepositoryImpl implements FcmRepository {
    private final FcmJpaRepository fcmJpaRepository;
    private final FcmPersistenceMapper mapper;

    @Override
    public void save(FCM fcm) {
        fcmJpaRepository.save(mapper.toJpaEntity(fcm));
    }

    @Override
    public Optional<FCM> findFCMByUserId(Long userId) {
        return fcmJpaRepository.findJpaFCMByMemberId(userId)
                .map(mapper::toDomainEntity);
//        return null;
    }
}
