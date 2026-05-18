package com.jotae.financeiro.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionRequestDTO {

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 3, max = 255, message = "A descrição deve ter entre 3 e 255 caracteres")
    private String description;

    @NotNull(message = "A data da transação é obrigatória")
    private LocalDateTime date;
    
    @NotNull(message = "A conta de origem (crédito) é obrigatória")
    private UUID sourceAccountId;

    @NotNull(message = "A conta de destino (débito) é obrigatória")
    private UUID destinationAccountId;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal amount;
}
