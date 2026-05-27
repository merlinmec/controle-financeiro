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
    private final User user;
    private final String description;
    private final LocalDateTime date;
    private final List<JournalEntry> entries;

    public Transaction(UUID id, User user, String description, LocalDateTime date, List<JournalEntry> entries) {
        this.id = id;
        this.user = user;
        this.description = description;
        this.date = date;
        this.entries = new ArrayList<>(entries);
        
        validateZeroSum();
        validateUserConsistency();
    }

    private Transaction(User user, String description, LocalDateTime date, List<JournalEntry> entries) {
        this(UUID.randomUUID(), user, description, date, entries);
    }

    public static Transaction create(User user, String description, LocalDateTime date, List<JournalEntry> entries) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (entries == null || entries.size() < 2) {
            throw new IllegalArgumentException("A transaction must have at least two entries (debit and credit).");
        }
        return new Transaction(user, description, date, entries);
    }

    public List<JournalEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    private void validateZeroSum() {
        BigDecimal sum = entries.stream()
                .map(e -> e.getAmount().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sum.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Invalid transaction: The sum of debits and credits is not zero. Final balance: " + sum);
        }
    }

    private void validateUserConsistency() {
        for (JournalEntry entry : this.entries) {
            if (!entry.getAccount().getUser().getId().equals(this.user.getId())) {
                throw new IllegalStateException("Transaction user and account user do not match.");
            }
        }
    }
}
