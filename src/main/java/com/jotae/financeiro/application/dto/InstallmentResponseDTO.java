package com.jotae.financeiro.application.dto;

import com.jotae.financeiro.domain.model.InstallmentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class InstallmentResponseDTO {
    private UUID id;
    private int number;
    private int totalInstallments;
    private BigDecimal amount;
    private LocalDate dueDate;
    private InstallmentStatus status;
    private UUID paymentTransactionId;
}
