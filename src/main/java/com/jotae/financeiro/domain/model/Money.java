package com.jotae.financeiro.domain.model;

import lombok.Value;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Value
public class Money {
    BigDecimal amount;
    Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Moeda não pode ser nula");
        }
        this.amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
        this.currency = currency;
    }

    public static Money ofBRL(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("BRL"));
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Não é possível operar com moedas diferentes");
        }
    }
}
