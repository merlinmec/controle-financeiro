package com.jotae.financeiro.infrastructure.persistence.mapper;

import com.jotae.financeiro.domain.model.Account;
import com.jotae.financeiro.infrastructure.persistence.entity.AccountEntity;

public class AccountMapper {

    public static AccountEntity toEntity(Account domain) {
        if (domain == null) return null;
        
        AccountEntity entity = new AccountEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setType(domain.getType());
        return entity;
    }

    public static Account toDomain(AccountEntity entity) {
        if (entity == null) return null;
        
        return Account.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .build();
    }
}
