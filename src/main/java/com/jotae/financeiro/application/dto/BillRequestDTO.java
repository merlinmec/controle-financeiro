package com.jotae.financeiro.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BillRequestDTO {

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 3, max = 255, message = "A descrição deve ter entre 3 e 255 caracteres")
    private String description;

    @NotNull(message = "O ID do usuário é obrigatório")
    private UUID userId;

    @NotNull(message = "A conta (categoria) é obrigatória")
    private UUID accountId;

    @NotNull(message = "O valor total ou da parcela é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal amount;

    @Min(value = 1, message = "O número de parcelas deve ser pelo menos 1")
    private int totalInstallments = 1;

    @NotNull(message = "A data de vencimento da primeira parcela é obrigatória")
    private LocalDate firstDueDate;
}