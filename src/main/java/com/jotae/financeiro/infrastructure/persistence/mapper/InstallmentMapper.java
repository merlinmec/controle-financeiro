package com.jotae.financeiro.infrastructure.persistence.mapper;

import com.jotae.financeiro.domain.model.Installment;
import com.jotae.financeiro.domain.model.Money;
import com.jotae.financeiro.infrastructure.persistence.entity.InstallmentEntity;

import java.util.Currency;

public class InstallmentMapper {

    public static InstallmentEntity toEntity(Installment domain) {
        if (domain == null) return null;

        InstallmentEntity entity = new InstallmentEntity();
        entity.setId(domain.getId());
        entity.setBill(BillMapper.toEntity(domain.getBill()));
        entity.setNumber(domain.getNumber());
        entity.setTotalInstallments(domain.getTotalInstallments());
        entity.setAmount(domain.getAmount().getAmount());
        entity.setCurrency(domain.getAmount().getCurrency().getCurrencyCode());
        entity.setDueDate(domain.getDueDate());
        entity.setStatus(domain.getStatus());
        entity.setPaymentTransaction(TransactionMapper.toEntity(domain.getPaymentTransaction()));
        return entity;
    }

    public static Installment toDomain(InstallmentEntity entity) {
        if (entity == null) return null;

        return new Installment(
                entity.getId(),
                BillMapper.toDomain(entity.getBill()),
                entity.getNumber(),
                entity.getTotalInstallments(),
                new Money(entity.getAmount(), Currency.getInstance(entity.getCurrency())),
                entity.getDueDate(),
                entity.getStatus(),
                TransactionMapper.toDomain(entity.getPaymentTransaction())
        );
    }
}
