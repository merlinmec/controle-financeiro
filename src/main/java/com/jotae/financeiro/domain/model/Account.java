package com.jotae.financeiro.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

/**
 * Pode ser um banco, uma categoria de despesa, ou um cartão de crédito.
 */
@Getter
@Builder
public class Account {
    private final UUID id;
    private final User user;
    private final String name;
    private final AccountType type;
    
    // Constructor without ID for new accounts
    public static Account create(User user, String name, AccountType type) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        return Account.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name(name)
                .type(type)
                .build();
    }
}
