package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.InstallmentResponseDTO;
import com.jotae.financeiro.domain.repository.InstallmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListInstallmentsUseCase {

    private final InstallmentRepository installmentRepository;

    public List<InstallmentResponseDTO> execute(UUID userId, LocalDate start, LocalDate end) {
        return installmentRepository.findByBillUserIdAndDueDateBetween(userId, start, end).stream()
                .map(inst -> InstallmentResponseDTO.builder()
                        .id(inst.getId())
                        .number(inst.getNumber())
                        .totalInstallments(inst.getTotalInstallments())
                        .amount(inst.getAmount().getAmount())
                        .dueDate(inst.getDueDate())
                        .status(inst.getStatus())
                        .paymentTransactionId(inst.getPaymentTransaction() != null ? inst.getPaymentTransaction().getId() : null)
                        .build())
                .collect(Collectors.toList());
    }
}
