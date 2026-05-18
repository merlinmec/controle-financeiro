package com.jotae.financeiro.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class JournalEntry {
    private final UUID id;
    private final Account account;
    private final Money amount; 

    public JournalEntry(UUID id, Account account, Money amount) {
        this.id = id;
        this.account = account;
        this.amount = amount;
    }

    private JournalEntry(Account account, Money amount) {
        this(UUID.randomUUID(), account, amount);
    }

    public static JournalEntry debit(Account account, Money amount) {
        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Débito deve ser positivo");
        }
        return new JournalEntry(account, amount);
    }

    public static JournalEntry credit(Account account, Money amount) {
        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Crédito deve ser positivo antes da inversão");
        }
        Money negativeAmount = new Money(amount.getAmount().negate(), amount.getCurrency());
        return new JournalEntry(account, negativeAmount);
    }
}