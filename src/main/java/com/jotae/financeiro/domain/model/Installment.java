package com.jotae.financeiro.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Installment {
    private final UUID id;
    private final Bill bill;
    private final int number;
    private final int totalInstallments;
    private final Money amount;
    private final LocalDate dueDate;
    
    private InstallmentStatus status;
    private Transaction paymentTransaction;

    public Installment(UUID id, Bill bill, int number, int totalInstallments, Money amount, LocalDate dueDate, InstallmentStatus status, Transaction paymentTransaction) {
        this.id = id;
        this.bill = bill;
        this.number = number;
        this.totalInstallments = totalInstallments;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
        this.paymentTransaction = paymentTransaction;
    }

    public static Installment createPending(Bill bill, int number, int totalInstallments, Money amount, LocalDate dueDate) {
        return new Installment(
                UUID.randomUUID(), 
                bill, 
                number, 
                totalInstallments, 
                amount, 
                dueDate, 
                InstallmentStatus.PENDING, 
                null
        );
    }

    public void markAsPaid(Transaction transaction) {
        if (this.status == InstallmentStatus.PAID) {
            throw new IllegalStateException("This installment is already paid.");
        }
        if (transaction == null) {
            throw new IllegalArgumentException("A valid payment transaction is required.");
        }

        this.status = InstallmentStatus.PAID;
        this.paymentTransaction = transaction;
    }
}