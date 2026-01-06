package com.hp.hospin.member.domain.form;

import lombok.Getter;

@Getter
public class LoginForm {
    private final String identifier;
    private final String password;

    public LoginForm(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }
}
