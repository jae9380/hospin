package com.hp.hospin.member.infrastructure.jpa;

import com.hp.hospin.member.infrastructure.entity.JpaMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJPARepository extends JpaRepository<JpaMemberEntity, Long> {
    boolean existsByIdentifier(String identifier);
}
