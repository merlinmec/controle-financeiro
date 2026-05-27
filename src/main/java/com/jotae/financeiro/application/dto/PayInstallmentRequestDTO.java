package com.jotae.financeiro.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PayInstallmentRequestDTO {

    @NotNull(message = "O ID do usuário é obrigatório")
    private UUID userId;

    @NotNull(message = "A conta de origem (pagamento) é obrigatória")
    private UUID sourceAccountId;

    @NotNull(message = "A data de pagamento é obrigatória")
    private LocalDateTime paymentDate;
}
