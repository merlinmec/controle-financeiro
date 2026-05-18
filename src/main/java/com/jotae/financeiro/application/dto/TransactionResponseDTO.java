package com.jotae.financeiro.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponseDTO {
    private UUID id;
    private String description;
    private LocalDateTime date;
    private BigDecimal amount;
    private String sourceAccountName;
    private String destinationAccountName;
}
