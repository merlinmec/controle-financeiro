package com.jotae.financeiro.domain.model;

public enum InstallmentStatus {
    PENDING,  // A pagar
    PAID,     // Paga
    OVERDUE,  // Vencida (Atrasada)
    CANCELED  // Cancelada (ex: compra estornada)
}
