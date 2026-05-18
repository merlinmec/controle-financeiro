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
    private final String name;
    private final AccountType type;
    
    public static Account create(String name, AccountType type) {
        return Account.builder()
                .id(UUID.randomUUID())
                .name(name)
                .type(type)
                .build();
    }
}
