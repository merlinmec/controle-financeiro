package com.jotae.financeiro.application.dto;

import com.jotae.financeiro.domain.model.AccountType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AccountResponseDTO {
    private UUID id;
    private String name;
    private AccountType type;
}
