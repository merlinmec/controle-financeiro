package com.jotae.financeiro.infrastructure.persistence.mapper;

import com.jotae.financeiro.domain.model.Bill;
import com.jotae.financeiro.infrastructure.persistence.entity.BillEntity;

public class BillMapper {

    public static BillEntity toEntity(Bill domain) {
        if (domain == null) return null;

        BillEntity entity = new BillEntity();
        entity.setId(domain.getId());
        entity.setUser(UserMapper.toEntity(domain.getUser()));
        entity.setDescription(domain.getDescription());
        entity.setAccount(AccountMapper.toEntity(domain.getAccount()));
        return entity;
    }

    public static Bill toDomain(BillEntity entity) {
        if (entity == null) return null;

        return Bill.builder()
                .id(entity.getId())
                .user(UserMapper.toDomain(entity.getUser()))
                .description(entity.getDescription())
                .account(AccountMapper.toDomain(entity.getAccount()))
                .build();
    }
}
