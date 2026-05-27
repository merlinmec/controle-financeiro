package com.jotae.financeiro.infrastructure.persistence.mapper;

import com.jotae.financeiro.domain.model.JournalEntry;
import com.jotae.financeiro.domain.model.Money;
import com.jotae.financeiro.domain.model.Transaction;
import com.jotae.financeiro.infrastructure.persistence.entity.JournalEntryEntity;
import com.jotae.financeiro.infrastructure.persistence.entity.TransactionEntity;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionMapper {

    public static TransactionEntity toEntity(Transaction domain) {
        if (domain == null) return null;

        TransactionEntity entity = new TransactionEntity();
        entity.setId(domain.getId());
        entity.setUser(UserMapper.toEntity(domain.getUser()));
        entity.setDescription(domain.getDescription());
        entity.setDate(domain.getDate());

        List<JournalEntryEntity> entryEntities = domain.getEntries().stream()
                .map(entryDomain -> toEntryEntity(entryDomain, entity))
                .collect(Collectors.toList());
        
        entity.setEntries(entryEntities);
        return entity;
    }

    private static JournalEntryEntity toEntryEntity(JournalEntry domain, TransactionEntity transactionEntity) {
        JournalEntryEntity entity = new JournalEntryEntity();
        entity.setId(domain.getId());
        entity.setTransaction(transactionEntity);
        entity.setAccount(AccountMapper.toEntity(domain.getAccount()));
        entity.setAmount(domain.getAmount().getAmount());
        entity.setCurrency(domain.getAmount().getCurrency().getCurrencyCode());
        return entity;
    }

    public static Transaction toDomain(TransactionEntity entity) {
        if (entity == null) return null;

        List<JournalEntry> entries = entity.getEntries().stream()
                .map(TransactionMapper::toEntryDomain)
                .collect(Collectors.toList());

        return new Transaction(entity.getId(), UserMapper.toDomain(entity.getUser()), entity.getDescription(), entity.getDate(), entries);
    }

    private static JournalEntry toEntryDomain(JournalEntryEntity entity) {
        Money money = new Money(entity.getAmount(), Currency.getInstance(entity.getCurrency()));
        return new JournalEntry(entity.getId(), AccountMapper.toDomain(entity.getAccount()), money);
    }
}