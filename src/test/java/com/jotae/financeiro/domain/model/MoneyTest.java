package com.jotae.financeiro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    private final Currency BRL = Currency.getInstance("BRL");
    private final Currency USD = Currency.getInstance("USD");

    @Test
    @DisplayName("Should correctly sum two amounts of the same currency")
    void shouldSumTwoAmounts() {
        // Arrange
        Money moneyA = new Money(new BigDecimal("10.50"), BRL);
        Money moneyB = new Money(new BigDecimal("5.25"), BRL);

        Money result = moneyA.add(moneyB);

        assertEquals(0, new BigDecimal("15.75").compareTo(result.getAmount()));
        assertEquals(BRL, result.getCurrency());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when summing different currencies")
    void shouldThrowExceptionWhenSummingDifferentCurrencies() {
        Money moneyBRL = new Money(new BigDecimal("10.00"), BRL);
        Money moneyUSD = new Money(new BigDecimal("10.00"), USD);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            moneyBRL.add(moneyUSD);
        });

        assertEquals("Não é possível operar com moedas diferentes", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for null amount or currency")
    void shouldThrowExceptionForNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Money(null, BRL);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Money(BigDecimal.TEN, null);
        });
    }

    @Test
    @DisplayName("Should correctly apply rounding to the specified scale")
    void shouldApplyRoundingCorrectly() {

        Money money = new Money(new BigDecimal("12.3456"), BRL);

        assertEquals(0, new BigDecimal("12.35").compareTo(money.getAmount()));
    }
}
