package com.hp.hospin.member.infrastructure.jpa;

import com.hp.hospin.member.infrastructure.entity.JpaMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJPARepository extends JpaRepository<JpaMemberEntity, Long> {
    boolean existsByIdentifier(String identifier);
    Optional<JpaMemberEntity> findJpaMemberEntityByIdentifier(String identifier);
}
