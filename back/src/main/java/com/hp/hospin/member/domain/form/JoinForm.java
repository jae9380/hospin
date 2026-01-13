package com.hp.hospin.member.domain.form;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class JoinForm {
     private final String identifier;
     private final String password;
     private final String name;
     private final String phoneNumber;
     private final String email;
     private final int gender;
     private final LocalDate birth;

    public JoinForm(String identifier, String password, String name, String phoneNumber, String email, int gender, LocalDate birth) {
        this.identifier = identifier;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.birth = birth;
    }
}
