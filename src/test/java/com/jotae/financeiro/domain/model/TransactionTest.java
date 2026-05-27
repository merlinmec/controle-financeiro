package com.jotae.financeiro.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private User testUser;
    private Account accountA;
    private Account accountB;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(java.util.UUID.randomUUID()).build();
        accountA = Account.create(testUser, "Account A", AccountType.ASSET);
        accountB = Account.create(testUser, "Account B", AccountType.EXPENSE);
    }

    @Test
    @DisplayName("Should create a valid zero-sum transaction")
    void shouldCreateValidZeroSumTransaction() {
        // Arrange
        JournalEntry debit = JournalEntry.debit(accountB, Money.ofBRL(new BigDecimal("100.00")));
        JournalEntry credit = JournalEntry.credit(accountA, Money.ofBRL(new BigDecimal("100.00")));

        // Act & Assert
        assertDoesNotThrow(() -> {
            Transaction.create(testUser, "Valid Transaction", LocalDateTime.now(), Arrays.asList(debit, credit));
        });
    }

    @Test
    @DisplayName("Should throw IllegalStateException for a non-zero-sum transaction")
    void shouldThrowExceptionForNonZeroSumTransaction() {
        // Arrange
        JournalEntry debit = JournalEntry.debit(accountB, Money.ofBRL(new BigDecimal("100.00")));
        JournalEntry credit = JournalEntry.credit(accountA, Money.ofBRL(new BigDecimal("99.99"))); // Mismatch

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            Transaction.create(testUser, "Invalid Transaction", LocalDateTime.now(), Arrays.asList(debit, credit));
        });

        assertTrue(exception.getMessage().contains("Invalid transaction: The sum of debits and credits is not zero"), 
            "A mensagem de erro deve conter o texto esperado.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for a transaction with less than two entries")
    void shouldThrowExceptionForLessThanTwoEntries() {
        // Arrange
        JournalEntry debit = JournalEntry.debit(accountB, Money.ofBRL(new BigDecimal("100.00")));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Transaction.create(testUser, "Single Entry Transaction", LocalDateTime.now(), Collections.singletonList(debit));
        });

        assertEquals("A transaction must have at least two entries (debit and credit).", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException if an account belongs to another user")
    void shouldThrowExceptionIfAccountBelongsToAnotherUser() {
        // Arrange
        User anotherUser = User.builder().id(java.util.UUID.randomUUID()).build();
        Account foreignAccount = Account.create(anotherUser, "Foreign Account", AccountType.ASSET);

        JournalEntry debit = JournalEntry.debit(accountB, Money.ofBRL(new BigDecimal("100.00")));
        JournalEntry credit = JournalEntry.credit(foreignAccount, Money.ofBRL(new BigDecimal("100.00")));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            Transaction.create(testUser, "Cross-user Transaction", LocalDateTime.now(), Arrays.asList(debit, credit));
        });

        assertEquals("Transaction user and account user do not match.", exception.getMessage());
    }
}
