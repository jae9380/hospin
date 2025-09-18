package com.hp.hospin.member.infrastructure.entity;

import com.hp.hospin.member.infrastructure.type.Gender;
import com.hp.hospin.member.infrastructure.type.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity(name="member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JpaMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifier;

    private String password;
    private String name;
    private String phoneNumber;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birth;
}
