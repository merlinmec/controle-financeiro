package com.jotae.financeiro.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BillResponseDTO {
    private UUID id;
    private String description;
    private String accountName;
    private int totalInstallments;
}