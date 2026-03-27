package com.hp.hospin.notification.infrastructure.jpa;

import com.hp.hospin.notification.infrastructure.entity.JpaFCM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmJpaRepository extends JpaRepository<JpaFCM, Long> {
    Optional<JpaFCM> findJpaFCMByMemberId(Long userId);
}
