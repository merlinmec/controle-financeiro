package com.jotae.financeiro.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class User {
    private final UUID id;
    private String name;
    private String email;
    private String password;

    public static User create(String name, String email, String password) {
        return User.builder()
                .id(UUID.randomUUID())
                .name(name)
                .email(email)
                .password(password)
                .build();
    }
}
