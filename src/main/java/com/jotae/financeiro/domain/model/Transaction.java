package com.jotae.financeiro.domain.model;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Transaction {
    private final UUID id;
    private final String description;
    private final LocalDateTime date;
    private final List<JournalEntry> entries;

    public Transaction(UUID id, String description, LocalDateTime date, List<JournalEntry> entries) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.entries = new ArrayList<>(entries);
        
        validateZeroSum();
    }

    private Transaction(String description, LocalDateTime date, List<JournalEntry> entries) {
        this(UUID.randomUUID(), description, date, entries);
    }

    public static Transaction create(String description, LocalDateTime date, List<JournalEntry> entries) {
        if (entries == null || entries.size() < 2) {
            throw new IllegalArgumentException("Uma transação precisa de pelo menos dois lançamentos (débito e crédito).");
        }
        return new Transaction(description, date, entries);
    }

    public List<JournalEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    private void validateZeroSum() {
        BigDecimal sum = entries.stream()
                .map(e -> e.getAmount().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sum.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Transação inválida: A soma dos débitos e créditos não é zero. Saldo final: " + sum);
        }
    }
}